package com.example.shopin.adapters

import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.shopin.R
import com.example.shopin.data.order.Order
import com.example.shopin.data.order.OrderStatus
import com.example.shopin.data.order.getOrderStatus
import com.example.shopin.databinding.OrderItemBinding

class AllOrdersAdapter:Adapter<AllOrdersAdapter.OrdersViewHolder> (){

    inner class OrdersViewHolder(private val binding: OrderItemBinding):ViewHolder(binding.root){
        fun bind(order: Order) {
            binding.apply {
                tvOrderId.text = order.orderId.toString()
                tvOrderDate.text = order.date

                val resources= itemView.resources

                val colorDrawable = when(getOrderStatus(order.orderStatus)){
                    is OrderStatus.Ordered->{
                        ColorDrawable(resources.getColor(R.color.g_orange_yellow))
                    }
                    is OrderStatus.Confirmed->{
                        ColorDrawable(resources.getColor(R.color.g_green))
                    }
                    is OrderStatus.Canceled->{
                        ColorDrawable(resources.getColor(R.color.g_red))
                    }
                    is OrderStatus.Shipped->{
                        ColorDrawable(resources.getColor(R.color.g_green))
                    }
                    is OrderStatus.Delivered->{
                        ColorDrawable(resources.getColor(R.color.g_green))
                    }
                    is OrderStatus.Returned->{
                        ColorDrawable(resources.getColor(R.color.g_red))
                    }
                }

                ivOrderState.setImageDrawable(colorDrawable)
            }
        }
    }

    private val diffCallback = object : DiffUtil.ItemCallback<Order>(){
        override fun areItemsTheSame(oldItem: Order, newItem: Order): Boolean {
            return oldItem.products == newItem.products
        }
        override fun areContentsTheSame(oldItem: Order, newItem: Order): Boolean {
            return oldItem==newItem
        }
    }

    val differ = AsyncListDiffer(this,diffCallback)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrdersViewHolder {
        return OrdersViewHolder(
            OrderItemBinding.inflate(
                LayoutInflater.from(parent.context),parent,false
            )
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: OrdersViewHolder, position: Int) {
        val order = differ.currentList[position]
        
        holder.bind(order)
    }

    var onClick :((Order)->Unit)? =null
}