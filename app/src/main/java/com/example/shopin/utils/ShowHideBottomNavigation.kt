package com.example.shopin.utils

import android.view.View
import androidx.fragment.app.Fragment
import com.example.shopin.R
import com.example.shopin.activities.ShoppingActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

fun Fragment.hideBottomNavigatioView(){
    val bottomNavigationView = (activity as ShoppingActivity).findViewById<BottomNavigationView>(
        R.id.bottomNavigation)

    bottomNavigationView.visibility = View.GONE
}

fun Fragment.showBottomNavigatioView(){
    val bottomNavigationView = (activity as ShoppingActivity).findViewById<BottomNavigationView>(
        R.id.bottomNavigation)

    bottomNavigationView.visibility = View.VISIBLE
}