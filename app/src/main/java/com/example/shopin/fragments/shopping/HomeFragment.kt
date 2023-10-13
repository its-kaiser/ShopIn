package com.example.shopin.fragments.shopping

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.shopin.R
import com.example.shopin.adapters.HomeViewpagerAdapter
import com.example.shopin.databinding.FragmentHomeBinding
import com.example.shopin.fragments.categories.AccessoryFragment
import com.example.shopin.fragments.categories.ChairFragment
import com.example.shopin.fragments.categories.CupboardFragment
import com.example.shopin.fragments.categories.FurnitureFragment
import com.example.shopin.fragments.categories.MainCategoryFragment
import com.example.shopin.fragments.categories.TableFragment
import com.google.android.material.tabs.TabLayoutMediator

class HomeFragment: Fragment(R.layout.fragment_home) {
    private lateinit var binding : FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val categoriesFragment = arrayListOf<Fragment>(
            MainCategoryFragment(),
            ChairFragment(),
            CupboardFragment(),
            TableFragment(),
            AccessoryFragment(),
            FurnitureFragment()
        )

        val viewpager2Adapter = HomeViewpagerAdapter(categoriesFragment,childFragmentManager,lifecycle)
        binding.viewpagerHome.adapter = viewpager2Adapter

        TabLayoutMediator(binding.tabLayout,binding.viewpagerHome){tab, position->
            when(position){
                0->tab.text = "Main"
                1->tab.text = "Chair"
                2->tab.text = "Cupboard"
                3->tab.text = "Table"
                4->tab.text = "Accessory"
                5->tab.text = "Furniture"
            }
        }.attach()
    }
}