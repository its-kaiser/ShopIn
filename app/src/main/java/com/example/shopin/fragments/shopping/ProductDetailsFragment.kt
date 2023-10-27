package com.example.shopin.fragments.shopping

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
import com.example.shopin.adapters.ColorsAdapter
import com.example.shopin.adapters.SizesAdapter
import com.example.shopin.adapters.ViewPager2Images
import com.example.shopin.data.Cart
import com.example.shopin.databinding.FragmentProductDetailsBinding
import com.example.shopin.utils.Resource
import com.example.shopin.utils.hideBottomNavigatioView
import com.example.shopin.viewmodel.DetailsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class ProductDetailsFragment: Fragment(){

    private val args by navArgs<ProductDetailsFragmentArgs>()
    private lateinit var binding: FragmentProductDetailsBinding
    private val viewPagerAdapter by lazy {ViewPager2Images()}
    private val sizesAdapter by lazy {SizesAdapter()}
    private val colorsAdapter by lazy {ColorsAdapter()}
    private var selectedColor:Int? = null
    private var selectedSize:String? = null

    private val viewModel by viewModels<DetailsViewModel>()

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

        sizesAdapter.onItemClick={
            selectedSize = it
        }

        colorsAdapter.onItemCLick = {
            selectedColor=it
        }

        binding.btnAddToCart.setOnClickListener {
            viewModel.addUpdateProductInCart(Cart(product,1,selectedColor,selectedSize))
        }

        lifecycleScope.launchWhenStarted {
            viewModel.addToCart.collectLatest {
                when(it){
                    is Resource.Loading ->{
                        binding.btnAddToCart.startAnimation()
                    }
                    is Resource.Success ->{
                        binding.btnAddToCart.revertAnimation()
                        binding.btnAddToCart.setBackgroundColor(resources.getColor(R.color.black))
                    }
                    is Resource.Error ->{
                        binding.btnAddToCart.revertAnimation()
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }
                    else->Unit
                }
            }
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