package com.example.shopin.fragments.categories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shopin.R
import com.example.shopin.adapters.BestProductAdapter
import com.example.shopin.databinding.FragmentBaseCategoryBinding

open class BaseCategoryFragment: Fragment(R.layout.fragment_base_category) {
    private lateinit var binding :FragmentBaseCategoryBinding

    protected val offerAdapter: BestProductAdapter by lazy { BestProductAdapter() }
    protected val bestProductAdapter: BestProductAdapter by lazy { BestProductAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBaseCategoryBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupOfferRv()
        setupBestProductsRv()

        binding.rvOffer.addOnScrollListener(object: RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                if(!recyclerView.canScrollHorizontally(1) && dx!=0){
                    onOfferPagingRequest()
                }
            }
        })

        binding.nestedScrollBaseCategory
            .setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener{ v, _, scrollY, _, _->
                if(v.getChildAt(0).bottom<=v.height+scrollY){
                    onBestProductsPagingRequest()
                }
            })
    }

    fun showOfferLoading(){
        binding.pbOfferProducts.visibility = View.VISIBLE
    }

    fun hideOfferLoading(){
        binding.pbOfferProducts.visibility = View.GONE
    }

    fun showBestProductsLoading(){
        binding.pbBestProducts.visibility = View.VISIBLE
    }

    fun hideBestProductsLoading(){
        binding.pbBestProducts.visibility = View.GONE
    }
    open fun onOfferPagingRequest(){

    }

    open fun onBestProductsPagingRequest(){

    }
    private fun setupBestProductsRv() {
        binding.rvBestProducts.apply {
            layoutManager = GridLayoutManager(requireContext(),2, GridLayoutManager.VERTICAL,false)
            adapter= bestProductAdapter
        }
    }

    private fun setupOfferRv() {
        binding.rvOffer.apply {
            layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
            adapter= offerAdapter
        }
    }
}