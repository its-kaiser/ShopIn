package com.example.shopin.adapters

import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.shopin.R
import com.example.shopin.data.Address
import com.example.shopin.databinding.AddressRvItemBinding

class AddressAdapter:RecyclerView.Adapter<AddressAdapter.AddressViewHolder>() {

    var selectedAddress  = -1
    inner class AddressViewHolder(val binding:AddressRvItemBinding):ViewHolder(binding.root){
        fun bind(address: Address, isSelected: Boolean) {

            binding.apply {
                btnAddress.text=address.addressTitle
                if(isSelected){
                    btnAddress.background = ColorDrawable(itemView.context.resources.getColor(R.color.g_blue))
                }
                else{
                    btnAddress.background = ColorDrawable(itemView.context.resources.getColor(R.color.g_white))
                }
            }
        }

    }

    private val diffCallback = object :DiffUtil.ItemCallback<Address>(){
        override fun areItemsTheSame(oldItem: Address, newItem: Address): Boolean {
            return oldItem.addressTitle== newItem.addressTitle &&
                    oldItem.fullName==newItem.fullName
        }

        override fun areContentsTheSame(oldItem: Address, newItem: Address): Boolean {
            return oldItem==newItem
        }
    }

    val differ = AsyncListDiffer(this,diffCallback)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressViewHolder {
        return AddressViewHolder(
            AddressRvItemBinding.inflate(LayoutInflater.from(parent.context))
        )
    }

    override fun getItemCount(): Int {
        return  differ.currentList.size
    }

    override fun onBindViewHolder(holder: AddressViewHolder, position: Int) {

        val address = differ.currentList[position]
        holder.bind(address,selectedAddress==position)

        holder.binding.btnAddress.setOnClickListener{
            if(selectedAddress>=0){
                notifyItemChanged(selectedAddress)
            }
            selectedAddress = holder.adapterPosition
            notifyItemChanged(selectedAddress)
            onClick?.invoke(address)
        }
    }


    var onClick: ((Address)->Unit)?=null

}