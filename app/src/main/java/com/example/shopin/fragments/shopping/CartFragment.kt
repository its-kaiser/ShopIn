package com.example.shopin.fragments.shopping

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shopin.R
import com.example.shopin.adapters.CartProductsAdapter
import com.example.shopin.databinding.FragmentCartBinding
import com.example.shopin.utils.Resource
import com.example.shopin.utils.VerticalItemDecoration
import com.example.shopin.viewmodel.CartViewModel
import kotlinx.coroutines.flow.collectLatest

class CartFragment: Fragment(R.layout.fragment_cart) {

    private lateinit var binding:FragmentCartBinding
    private val cartAdapter by lazy { CartProductsAdapter() }
    private val viewModel by activityViewModels<CartViewModel> ()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentCartBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupCartRv()

        lifecycleScope.launchWhenStarted {
            viewModel.productPrice.collectLatest {price->
                price?.let {
                    binding.tvTotalPrice.text = price.toString()
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.cartProducts.collectLatest {
                when(it){
                    is Resource.Loading->{
                        binding.pbCart.visibility = View.VISIBLE
                    }
                    is Resource.Success->{
                        binding.pbCart.visibility = View.INVISIBLE

                        if(it.data!!.isEmpty()){
                            showEmptyCart()
                            hideOtherViews()
                        }
                        else{
                            hideEmptyCart()
                            showOtherViews()
                            cartAdapter.differ.submitList(it.data)
                        }
                    }
                    is Resource.Error->{
                        binding.pbCart.visibility = View.VISIBLE
                        Toast.makeText(requireContext(),it.message.toString(),Toast.LENGTH_SHORT).show()
                    }
                    else->Unit
                }
            }
        }

        cartAdapter.onProductClick ={
            val bundle = Bundle().apply { putParcelable("product",it.product) }
            findNavController().navigate(R.id.action_cartFragment_to_productDetailsFragment,bundle)
        }
    }

    private fun showOtherViews() {
        binding.apply {
            rvCart.visibility = View.VISIBLE
            totalBoxContainer.visibility =View.VISIBLE
            btnCheckout.visibility = View.VISIBLE
        }
    }

    private fun hideOtherViews() {
        binding.apply {
            rvCart.visibility = View.GONE
            totalBoxContainer.visibility =View.GONE
            btnCheckout.visibility = View.GONE
        }
    }

    private fun hideEmptyCart() {
        binding.apply {
            cartEmpty.visibility = View.GONE
        }
    }

    private fun showEmptyCart() {
        binding.apply {
            cartEmpty.visibility = View.VISIBLE
        }
    }

    private fun setupCartRv() {
        binding.rvCart.apply {
            layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
            adapter=cartAdapter
            addItemDecoration(VerticalItemDecoration())
        }
    }
}