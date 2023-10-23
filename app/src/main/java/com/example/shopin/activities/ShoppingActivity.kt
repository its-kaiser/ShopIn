package com.example.shopin.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.shopin.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ShoppingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shopping)
    }
}