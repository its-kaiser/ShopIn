package com.example.shopin.fragments.loginRegister

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.shopin.R
import com.example.shopin.activities.ShoppingActivity
import com.example.shopin.databinding.FragmentLoginBinding
import com.example.shopin.dialog.setupBottomSheetDialog
import com.example.shopin.utils.Resource
import com.example.shopin.viewmodel.LoginViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment: Fragment(R.layout.fragment_login) {
    private lateinit var binding: FragmentLoginBinding
    private val viewModel by viewModels<LoginViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentLoginBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvDontHaveAnAccount.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        binding.apply {
            btnLoginLogin.setOnClickListener {
                val email = etEmailLogin.text.toString().trim()
                val password = etPasswordLogin.text.toString()

                viewModel.login(email,password)
            }
        }

        binding.tvForgotPasswordLogin.setOnClickListener {
            setupBottomSheetDialog { email->
                viewModel.resetPassword(email)
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.resetPassword.collect{
                when(it){
                    is Resource.Loading ->{

                    }
                    is Resource.Error ->{
                        Snackbar.make(requireView(), "Error: ${it.message}", Snackbar.LENGTH_LONG).show()
                    }
                    is Resource.Success->{
                        Snackbar.make(requireView(), "Reset link has been sent to your email", Snackbar.LENGTH_LONG).show()
                    }
                    else->Unit
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.login.collect{
                when(it){
                    is Resource.Loading ->{
                        binding.btnLoginLogin.startAnimation()
                    }
                    is Resource.Error ->{
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                        binding.btnLoginLogin.revertAnimation()
                    }
                    is Resource.Success->{
                        binding.btnLoginLogin.revertAnimation()
                        Intent(requireActivity(), ShoppingActivity::class.java).also{ intent->
                            //for clearing the stack so that we don't come back to login and register activity once logged in
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                        }
                    }
                    else->Unit
                }
            }
        }
    }
}