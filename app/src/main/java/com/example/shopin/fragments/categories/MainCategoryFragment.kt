package com.example.shopin.fragments.categories

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shopin.R
import com.example.shopin.adapters.BestDealsAdapter
import com.example.shopin.adapters.BestProductAdapter
import com.example.shopin.adapters.SpecialProductsAdapter
import com.example.shopin.databinding.FragmentMainCategoryBinding
import com.example.shopin.utils.Resource
import com.example.shopin.utils.showBottomNavigatioView
import com.example.shopin.viewmodel.MainCategoryViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class MainCategoryFragment:Fragment(R.layout.fragment_main_category) {

    companion object{
        private const val TAG="MainCategoryFragment"
    }
    private lateinit var binding : FragmentMainCategoryBinding
    private lateinit var specialProductsAdapter: SpecialProductsAdapter
    private lateinit var bestDealsAdapter: BestDealsAdapter
    private lateinit var bestProductAdapter: BestProductAdapter
    private val viewModel by viewModels<MainCategoryViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentMainCategoryBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        setupSpecialProductsRv()
        setupBestDealsRv()
        setupBestProductsRv()

        specialProductsAdapter.onClick = {
            val bund = Bundle().apply { putParcelable("product",it) }
            findNavController().navigate(R.id.action_homeFragment_to_productDetailsFragment,bund)
        }

        bestProductAdapter.onClick = {
            val bund = Bundle().apply { putParcelable("product",it) }
            findNavController().navigate(R.id.action_homeFragment_to_productDetailsFragment,bund)
        }

        bestDealsAdapter.onClick = {
            val bund = Bundle().apply { putParcelable("product",it) }
            findNavController().navigate(R.id.action_homeFragment_to_productDetailsFragment,bund)
        }
        lifecycleScope.launchWhenStarted {
            viewModel.specialProducts.collectLatest {
                when(it){
                    is Resource.Loading->{
                        showLoading()
                    }
                    is Resource.Success->{
                        specialProductsAdapter.differ.submitList(it.data)
                        hideLoading()
                    }
                    is Resource.Error->{
                        hideLoading()
                        Log.e(TAG,it.message.toString())
                        Toast.makeText(requireContext(),it.message,Toast.LENGTH_SHORT).show()
                    }
                    else->Unit
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.bestDealsProducts.collectLatest {
                when(it){
                    is Resource.Loading->{
                        showLoading()
                    }
                    is Resource.Success->{
                        bestDealsAdapter.differ.submitList(it.data)
                        hideLoading()
                    }
                    is Resource.Error->{
                        hideLoading()
                        Log.e(TAG,it.message.toString())
                        Toast.makeText(requireContext(),it.message,Toast.LENGTH_SHORT).show()
                    }
                    else->Unit
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.bestProducts.collectLatest {
                when(it){
                    is Resource.Loading->{
                        binding.bestProductsProgressBar.visibility = View.VISIBLE
                    }
                    is Resource.Success->{
                        bestProductAdapter.differ.submitList(it.data)
                        binding.bestProductsProgressBar.visibility = View.GONE
                    }
                    is Resource.Error->{
                        binding.bestProductsProgressBar.visibility = View.GONE
                        Log.e(TAG,it.message.toString())
                        Toast.makeText(requireContext(),it.message,Toast.LENGTH_SHORT).show()
                    }
                    else->Unit
                }
            }
        }
        bestProductsPaging()
        bestDealsPaging()
        specialProductsPaging()
    }



    override fun onResume() {
        super.onResume()

        showBottomNavigatioView()
    }

    private fun setupBestProductsRv() {
        bestProductAdapter = BestProductAdapter()
        binding.rvBestProducts.apply {
            layoutManager = GridLayoutManager(requireContext(),2,GridLayoutManager.VERTICAL,false)
            adapter= bestProductAdapter
        }
    }

    private fun setupBestDealsRv() {
        bestDealsAdapter = BestDealsAdapter()
        binding.rvBestDealsProducts.apply {
            layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
            adapter=bestDealsAdapter
        }
    }

    private fun hideLoading() {
        binding.pbMainCategoryProgressBar.visibility=View.GONE
    }

    private fun showLoading() {
        binding.pbMainCategoryProgressBar.visibility=View.VISIBLE
    }

    private fun setupSpecialProductsRv() {
        specialProductsAdapter = SpecialProductsAdapter()
        binding.rvSpecialProducts.apply {
            layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
            adapter=specialProductsAdapter
        }
    }

    private fun bestProductsPaging(){
        binding.nestedScrollMainCategory
            .setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener{ v, _, scrollY, _, _->
                if(v.getChildAt(0).bottom<=v.height+scrollY){
                    viewModel.fetchBestProducts()
                }
            })
    }

    private fun specialProductsPaging() {

        binding.rvSpecialProducts.addOnScrollListener(object :RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                if(!recyclerView.canScrollVertically(1) && dx!=0){
                    viewModel.fetchSpecialProducts()
                }
            }
        })
    }
    private fun bestDealsPaging() {

        binding.rvBestDealsProducts.addOnScrollListener(object :RecyclerView.OnScrollListener(){

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                if(!recyclerView.canScrollHorizontally(1) && dx!=0){
                    viewModel.fetchBestDeals()
                }
            }
        })
    }

}