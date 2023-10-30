package com.example.shopin.fragments.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shopin.adapters.BillingProductsAdapter
import com.example.shopin.data.order.OrderStatus
import com.example.shopin.data.order.getOrderStatus
import com.example.shopin.databinding.FragmentOrderDetailBinding
import com.example.shopin.utils.VerticalItemDecoration

class OrderDetailFragment:Fragment() {

    lateinit var binding:FragmentOrderDetailBinding
    private val billingAdapter by lazy { BillingProductsAdapter() }
    private val args by navArgs<OrderDetailFragmentArgs>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOrderDetailBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val order = args.order
        setupOrderRv()

        binding.apply {
            tvOrderId.text = "Order #${order.orderId}"

            stepView.setSteps(
                mutableListOf(
                    OrderStatus.Ordered.status,
                    OrderStatus.Confirmed.status,
                    OrderStatus.Shipped.status,
                    OrderStatus.Delivered.status
                )
            )

            val currentOrderState = when(getOrderStatus(order.orderStatus)){
                is OrderStatus.Ordered->0
                is OrderStatus.Confirmed->1
                is OrderStatus.Shipped->2
                is OrderStatus.Delivered->3
                else->0
            }

            stepView.go(currentOrderState,false)

            if(currentOrderState==3){
                stepView.done(true)
            }

            tvFullName.text = order.address.fullName
            tvAddress.text = "${order.address.street} ${order.address.city}"
            tvPhoneNumber.text = order.address.phoneNumber
            tvTotalPrice.text = order.totalPrice.toString()
        }

        billingAdapter.differ.submitList(order.products)
    }

    private fun setupOrderRv() {
        binding.rvProducts.apply {
            layoutManager= LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
            adapter= billingAdapter
            addItemDecoration(VerticalItemDecoration())
        }
    }


}