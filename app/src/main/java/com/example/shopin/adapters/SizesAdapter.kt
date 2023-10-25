package com.example.shopin.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.shopin.databinding.SizeRvItemBinding

class SizesAdapter:RecyclerView.Adapter<SizesAdapter.SizeViewHolder>() {

    private var selectedSize = -1
    inner class SizeViewHolder(val binding:SizeRvItemBinding):ViewHolder(binding.root) {
        fun bind(size: String, position: Int) {
            binding.tvSize.text = size

            //size is selected
            if(position==selectedSize){
                binding.ivShadow.visibility = View.VISIBLE
            }
            //size is not selected
            else{
                binding.ivShadow.visibility = View.INVISIBLE
            }
        }
    }

    private val diffCallback = object : DiffUtil.ItemCallback<String>(){
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem==newItem
        }
        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem==newItem
        }
    }

    val differ = AsyncListDiffer(this, diffCallback)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SizeViewHolder {
        return SizeViewHolder(
            SizeRvItemBinding.inflate(
                LayoutInflater.from(parent.context)
            )
        )
    }

    override fun getItemCount(): Int {
        return  differ.currentList.size
    }

    override fun onBindViewHolder(holder: SizeViewHolder, position: Int) {
        val size = differ.currentList[position]

        holder.bind(size,position)

        holder.itemView.setOnClickListener{
            if(selectedSize>=0){
                notifyItemChanged(selectedSize)
            }

            selectedSize=holder.adapterPosition
            onItemClick?.invoke(size)
        }
    }
    var onItemClick: ((String)->Unit)? = null
}