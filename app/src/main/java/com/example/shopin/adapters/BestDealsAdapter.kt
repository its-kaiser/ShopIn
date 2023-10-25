package com.example.shopin.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.shopin.data.Product
import com.example.shopin.databinding.BestDealsRvItemBinding

class BestDealsAdapter:RecyclerView.Adapter<BestDealsAdapter.BestDealViewHolder>() {

    inner class BestDealViewHolder(
        private val binding:BestDealsRvItemBinding):RecyclerView.ViewHolder(binding.root){
        fun bind(product: Product) {
            binding.apply {
                Glide.with(itemView).load(product.images[0]).into(ivBestDeal)
                product.offerPercentage?.let{
                    val remainingPricePerc = 1f-it
                    val priceAfterOffer = remainingPricePerc*product.price
                    tvNewPrice.text = String.format("%.2f",priceAfterOffer)
                }
                tvOldPrice.text = product.price.toString()
                tvDealProductName.text = product.name
            }
        }

    }

    private val diffCallback = object: DiffUtil.ItemCallback<Product>(){
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id==newItem.id
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
           return oldItem==newItem
        }
    }
    val differ = AsyncListDiffer(this,diffCallback)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BestDealViewHolder {
        return BestDealViewHolder(
            BestDealsRvItemBinding.inflate(
                LayoutInflater.from(parent.context)
            )
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: BestDealViewHolder, position: Int) {
        val product = differ.currentList[position]
        holder.bind(product)

        holder.itemView.setOnClickListener{
            onClick?.invoke(product)
        }
    }

    var onClick:((Product)->Unit)?=null
}