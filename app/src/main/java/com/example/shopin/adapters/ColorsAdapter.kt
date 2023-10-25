package com.example.shopin.adapters

import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.shopin.databinding.ColorRvItemBinding

class ColorsAdapter:RecyclerView.Adapter<ColorsAdapter.ColorViewHolder>() {

    private var selectedColor =-1
    inner class ColorViewHolder(val binding:ColorRvItemBinding):ViewHolder(binding.root) {
        fun bind(color: Int, position: Int) {

            val imageDrawable = ColorDrawable(color)

            binding.ivColor.setImageDrawable(imageDrawable)
            //color is selected
            if(position==selectedColor){
                binding.apply {
                    ivShadow.visibility = View.VISIBLE
                    ivColorPicker.visibility = View.VISIBLE
                }
            }
            //color not selected
            else{
                binding.apply {
                    ivShadow.visibility = View.INVISIBLE
                    ivColorPicker.visibility = View.INVISIBLE
                }
            }
        }
    }

    private val diffCallback =object :DiffUtil.ItemCallback<Int>(){
        override fun areItemsTheSame(oldItem: Int, newItem: Int): Boolean {
            return oldItem==newItem
        }

        override fun areContentsTheSame(oldItem: Int, newItem: Int): Boolean {
            return oldItem==newItem
        }

    }

    val differ = AsyncListDiffer(this,diffCallback)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorViewHolder {
        return ColorViewHolder(
            ColorRvItemBinding.inflate(
                LayoutInflater.from(parent.context)
            )
        )
    }

    override fun getItemCount(): Int {
       return differ.currentList.size
    }

    override fun onBindViewHolder(holder: ColorViewHolder, position: Int) {
        val color = differ.currentList[position]
        holder.bind(color,position)

        holder.itemView.setOnClickListener{
            if(selectedColor>=0){
                notifyItemChanged(selectedColor)
            }
            selectedColor = holder.adapterPosition
            notifyItemChanged(selectedColor)
            onItemCLick?.invoke(color)
        }
    }

    var onItemCLick:((Int) ->Unit)? =null
}