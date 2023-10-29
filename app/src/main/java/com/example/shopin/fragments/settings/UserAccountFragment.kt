package com.example.shopin.fragments.settings

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.shopin.data.User
import com.example.shopin.databinding.FragmentUserAccountBinding
import com.example.shopin.dialog.setupBottomSheetDialog
import com.example.shopin.utils.Resource
import com.example.shopin.viewmodel.UserAccountViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class UserAccountFragment: Fragment() {

    private lateinit var binding:FragmentUserAccountBinding
    private val viewModel by viewModels<UserAccountViewModel>()
    private lateinit var imageActivityLauncher :ActivityResultLauncher<Intent>
    private var imageUri : Uri? =null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        imageActivityLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            imageUri =it.data?.data
            Glide.with(this).load(imageUri).into(binding.ivUser)
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentUserAccountBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launchWhenStarted {
            viewModel.user.collectLatest {
                when(it){
                    is Resource.Loading->{
                        showUserLoading()
                    }
                    is Resource.Success->{
                        hideUserLoading()
                        showUserInfo(it.data!!)
                    }
                    is Resource.Error->{
                        hideUserLoading()
                    }
                    else->Unit
                }
            }
        }
        lifecycleScope.launchWhenStarted {
            viewModel.updateInfo.collectLatest {
                when(it){
                    is Resource.Loading->{
                        binding.btnSaveAccount.startAnimation()
                    }
                    is Resource.Success->{
                        binding.btnSaveAccount.revertAnimation()
                        findNavController().navigateUp()
                    }
                    is Resource.Error->{
                        binding.btnSaveAccount.revertAnimation()
                    }
                    else->Unit
                }
            }
        }

        binding.btnSaveAccount.setOnClickListener {
            binding.apply {
                val firstName = etFirstName.text.trim().toString()
                val lastName = etLastName.text.trim().toString()
                val email = etEmailAccount.text.trim().toString()

                val user = User(firstName, lastName, email)
                viewModel.updateUserInfo(user, imageUri)
            }
        }

        binding.ivEdit.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type= "image/*"
            imageActivityLauncher.launch(intent)
        }

        binding.tvUpdatePassword.setOnClickListener {
            setupBottomSheetDialog {email->
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
    }

    private fun showUserInfo(data: User) {
        binding.apply {
            Glide.with(this@UserAccountFragment).load(data.imagePath).error(ColorDrawable(Color.BLACK)).into(ivUser)
            etFirstName.setText(data.firstName)
            etLastName.setText(data.lastName)
            etEmailAccount.setText(data.email)
        }
    }

    private fun hideUserLoading() {
        binding.apply {
            progressbarAccount.visibility = View.GONE
            ivUser.visibility= View.VISIBLE
            ivEdit.visibility= View.VISIBLE
            etFirstName.visibility= View.VISIBLE
            etLastName.visibility= View.VISIBLE
            etEmailAccount.visibility= View.VISIBLE
            tvUpdatePassword.visibility= View.VISIBLE
            btnSaveAccount.visibility= View.VISIBLE
        }
    }

    private fun showUserLoading() {
        binding.apply {
            progressbarAccount.visibility = View.VISIBLE
            ivUser.visibility= View.INVISIBLE
            ivEdit.visibility= View.INVISIBLE
            etFirstName.visibility= View.INVISIBLE
            etLastName.visibility= View.INVISIBLE
            etEmailAccount.visibility= View.INVISIBLE
            tvUpdatePassword.visibility= View.INVISIBLE
            btnSaveAccount.visibility= View.INVISIBLE
        }
    }
}