package com.example.shopin.fragments.shopping

import android.app.AlertDialog
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
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shopin.R
import com.example.shopin.adapters.AddressAdapter
import com.example.shopin.adapters.BillingProductsAdapter
import com.example.shopin.data.Address
import com.example.shopin.data.Cart
import com.example.shopin.data.Order
import com.example.shopin.data.OrderStatus
import com.example.shopin.databinding.FragmentBillingBinding
import com.example.shopin.utils.HorizontalItemDecoration
import com.example.shopin.utils.Resource
import com.example.shopin.viewmodel.BillingViewModel
import com.example.shopin.viewmodel.OrderViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class BillingFragment:Fragment() {

    private lateinit var binding:FragmentBillingBinding
    private val billingAdapter by lazy {BillingProductsAdapter()}
    private val addressAdapter by lazy {AddressAdapter()}
    private val billingViewModel by viewModels<BillingViewModel>()

    private val args by navArgs<BillingFragmentArgs>()
    private var products = emptyList<Cart>()
    private var totalPrice = 0f

    private var selectedAddress:Address? = null
    private val orderViewModel by viewModels<OrderViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        products= args.cartProducts.toList()
        totalPrice= args.totalPrice
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBillingBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupBillingProductsRv()
        setupAddressRv()

        lifecycleScope.launchWhenStarted {
            billingViewModel.address.collectLatest {
                when(it){
                    is Resource.Loading->{
                        binding.progressbarAddress.visibility= View.VISIBLE
                    }
                    is Resource.Success->{
                        binding.progressbarAddress.visibility = View.GONE
                        addressAdapter.differ.submitList(it.data)
                    }
                    is Resource.Error->{
                        binding.progressbarAddress.visibility = View.GONE
                        Toast.makeText(requireContext(),it.message,Toast.LENGTH_SHORT).show()
                    }
                    else-> Unit
                }
            }
        }

        billingAdapter.differ.submitList(products)
        binding.tvTotalPrice.text= totalPrice.toString()
        binding.ivAddAddress.setOnClickListener {
            findNavController().navigate(R.id.action_billingFragment_to_addressFragment)
        }

        addressAdapter.onClick={
            selectedAddress=it
        }
        binding.btnPlaceOrder.setOnClickListener {

            if(selectedAddress==null){
                Toast.makeText(requireContext(),"Please select an address",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            showOrderConfirmationDialog()
        }

        lifecycleScope.launchWhenStarted {
            orderViewModel.order.collectLatest {
                when(it){
                    is Resource.Loading->{
                       binding.btnPlaceOrder.startAnimation()
                    }
                    is Resource.Success->{
                        binding.btnPlaceOrder.revertAnimation()
                        findNavController().navigateUp()
                        Snackbar.make(requireView(),"Your order was placed successfully",Snackbar.LENGTH_LONG).show()
                    }
                    is Resource.Error->{
                        binding.btnPlaceOrder.revertAnimation()
                        Toast.makeText(requireContext(),it.message,Toast.LENGTH_SHORT).show()
                    }
                    else-> Unit
                }
            }
        }
    }

    private fun showOrderConfirmationDialog() {
        val alertDialog = AlertDialog.Builder(requireContext()).apply {
            setTitle("Order items")
            setMessage("Do you want to order your cart items?")
            setNegativeButton("Cancel"){dialog,_->
                dialog.dismiss()
            }
            setPositiveButton("Yes"){dialog,_->
                val order = Order(
                    OrderStatus.Ordered.status,
                    totalPrice,
                    products,
                    selectedAddress!!
                )
                orderViewModel.placeOrder(order)
                dialog.dismiss()
            }
        }
        alertDialog.create()
        alertDialog.show()
    }

    private fun setupAddressRv() {
        binding.rvAddress.apply {
            layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
            adapter = addressAdapter
            addItemDecoration(HorizontalItemDecoration())
        }
    }

    private fun setupBillingProductsRv() {
        binding.rvProducts.apply {
            layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
            adapter = billingAdapter
            addItemDecoration(HorizontalItemDecoration())
        }
    }
}