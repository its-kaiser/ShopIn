package com.example.shopin.adapters

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.shopin.data.Cart
import com.example.shopin.databinding.CartProductItemBinding
import com.example.shopin.helper.getProductPrice

class CartProductsAdapter : RecyclerView.Adapter<CartProductsAdapter.CartProductsViewHolder>() {

    inner class CartProductsViewHolder(
        val binding: CartProductItemBinding
    ): RecyclerView.ViewHolder(binding.root) {
        fun bind(cartProduct: Cart) {
            binding.apply {
                Glide.with(itemView).load(cartProduct.product.images[0]).into(ivCartProduct)
                tvCartProductName.text = cartProduct.product.name
                tvCartProductQuantity.text = cartProduct.quantity.toString()

                val priceAfterOffer = cartProduct.product.offerPercentage.getProductPrice(cartProduct.product.price)
                tvCartProductPrice.text = String.format("%.2f",priceAfterOffer)

                ivCartProductColor.setImageDrawable(ColorDrawable(cartProduct.selectedColor?:Color.TRANSPARENT))
                tvCartProductSize.text = cartProduct.selectedSize?:"".also { ivCartProductSize.setImageDrawable(ColorDrawable(Color.TRANSPARENT)) }
            }
        }
    }

    private val diffCallback = object: DiffUtil.ItemCallback<Cart>(){
        override fun areItemsTheSame(oldItem: Cart, newItem: Cart): Boolean {
            return oldItem.product.id == newItem.product.id
        }

        override fun areContentsTheSame(oldItem: Cart, newItem: Cart): Boolean {
            return oldItem==newItem
        }
    }
    val differ = AsyncListDiffer(this,diffCallback)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartProductsViewHolder {
        return CartProductsViewHolder(
            CartProductItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: CartProductsViewHolder, position: Int) {
        val cartProduct = differ.currentList[position]
        holder.bind(cartProduct)

        holder.itemView.setOnClickListener{
            onProductClick?.invoke(cartProduct)
        }

        holder.binding.plusIcon.setOnClickListener{
            onPlusClick?.invoke(cartProduct)
        }

        holder.binding.minusIcon.setOnClickListener{
            onMinusClick?.invoke(cartProduct)
        }
    }

    var onProductClick:((Cart)->Unit)?=null

    var onPlusClick:((Cart)->Unit)?=null
    var onMinusClick:((Cart)->Unit)?=null
}