package com.example.shopin.fragments.shopping

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shopin.adapters.ColorsAdapter
import com.example.shopin.adapters.SizesAdapter
import com.example.shopin.adapters.ViewPager2Images
import com.example.shopin.databinding.FragmentProductDetailsBinding
import com.example.shopin.utils.hideBottomNavigatioView

class ProductDetailsFragment: Fragment(){

    private val args by navArgs<ProductDetailsFragmentArgs>()
    private lateinit var binding: FragmentProductDetailsBinding
    private val viewPagerAdapter by lazy {ViewPager2Images()}
    private val sizesAdapter by lazy {SizesAdapter()}
    private val colorsAdapter by lazy {ColorsAdapter()}

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        hideBottomNavigatioView()
        binding = FragmentProductDetailsBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val product = args.product

        setupSizesRv()
        setupColorsRv()
        setupViewpager()

        binding.ivClose.setOnClickListener{
            findNavController().navigateUp()
        }
        binding.apply {
            tvProductName.text = product.name
            tvProductPrice.text = product.price.toString()
            tvProductDescription.text = product.description

            if(product.colors.isNullOrEmpty()){
                tvProductColors.visibility = View.INVISIBLE
            }
            if(product.sizes.isNullOrEmpty()){
                tvProductSizes.visibility = View.INVISIBLE
            }
        }

        viewPagerAdapter.differ.submitList(product.images)
        product.colors?.let {
            colorsAdapter.differ.submitList(product.colors)
        }
        product.sizes?.let {
            sizesAdapter.differ.submitList(product.sizes)
        }
    }

    private fun setupViewpager() {
        binding.apply {
            viewPagerProductImages.adapter = viewPagerAdapter
        }
    }

    private fun setupColorsRv() {
        binding.rvColors.apply {
            adapter=colorsAdapter
            layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
        }
    }

    private fun setupSizesRv() {
        binding.rvSizes.apply {
            adapter=sizesAdapter
            layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
        }
    }
}