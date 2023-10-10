package com.example.shopin.fragments.loginRegister

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.shopin.data.User
import com.example.shopin.databinding.FragmentRegisterBinding
import com.example.shopin.utils.RegisterValidation
import com.example.shopin.utils.Resource
import com.example.shopin.viewmodel.RegisterViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class RegisterFragment: Fragment() {

    companion object{
        const val TAG="RegisterFragment"
    }
    private lateinit var binding : FragmentRegisterBinding
    private val viewModel by viewModels<RegisterViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        binding = FragmentRegisterBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            btnRegisterRegister.setOnClickListener {
                val user =  User(
                    etFirstNameRegister.text.toString().trim(),
                    etLastNameRegister.text.toString().trim(),
                    etEmailRegister.text.toString().trim()
                )
                val password = etPasswordRegister.text.toString()

                viewModel.createAccountWithEmail(user,password)
            }
        }
        lifecycleScope.launchWhenStarted {
            viewModel.register.collect{
                when(it){
                    is Resource.Loading ->{
                        binding.btnRegisterRegister.startAnimation()
                    }
                    is Resource.Success ->{
                        Log.d("test",it.data.toString())
                        binding.btnRegisterRegister.revertAnimation()
                    }
                    is Resource.Error ->{
                        Log.e(TAG,it.message.toString())
                        binding.btnRegisterRegister.revertAnimation()
                    }
                    else ->Unit
                }
            }
        }
        lifecycleScope.launchWhenStarted {
            viewModel.validation.collect{validation ->
                if(validation.email is RegisterValidation.Failed){
                    withContext(Dispatchers.Main){
                        binding.etEmailRegister.apply{
                            requestFocus()
                            error= validation.email .message
                        }
                    }
                }
                if(validation.password is RegisterValidation.Failed){
                    withContext(Dispatchers.Main){
                        binding.etPasswordRegister.apply{
                            requestFocus()
                            error= validation.password.message
                        }
                    }
                }
            }
        }
    }
}