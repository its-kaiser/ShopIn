package com.example.shopin.fragments.shopping

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.shopin.data.Address
import com.example.shopin.databinding.FragmentAddressBinding
import com.example.shopin.utils.Resource
import com.example.shopin.viewmodel.AddressViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class AddressFragment:Fragment() {

    private lateinit var binding: FragmentAddressBinding
    val viewModel by viewModels<AddressViewModel>()
    val args by navArgs<AddressFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentAddressBinding.inflate(inflater)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val address= args.address

        if(address==null){
            binding.btnDelete.visibility = View.GONE
        }
        else{
            binding.apply {
                edAddressTitle.setText(address.addressTitle)
                edCity.setText(address.city)
                edPhone.setText(address.phoneNumber)
                edState.setText(address.state)
                edFullName.setText(address.fullName)
                edStreet.setText(address.street)
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.addNewAddress.collectLatest {
                when(it){
                    is Resource.Loading->{
                        binding.progressbarAddress.visibility = View.VISIBLE
                    }
                    is Resource.Success->{
                         binding.progressbarAddress.visibility = View.INVISIBLE
                        findNavController().navigateUp()
                    }
                    is Resource.Error->{
                        Toast.makeText(requireContext(),it.message,Toast.LENGTH_SHORT).show()
                    }
                    else->Unit
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.error.collectLatest {
                Toast.makeText(requireContext(),it,Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            btnSave.setOnClickListener{
                val addressTitle =edAddressTitle.text.toString()
                val fullName = edFullName.text.toString()
                val street = edStreet.text.toString()
                val city = edCity.text.toString()
                val phoneNumber = edPhone.text.toString()
                val state = edState.text.toString()
                val address = Address(addressTitle,fullName,street,phoneNumber,city,state)

                viewModel.addAddress(address)
            }
        }
    }
}