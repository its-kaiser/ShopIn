package com.example.shopin.data

data class Order (
    val orderStatus: String,
    val totalPrice: Float,
    val products: List<Cart>,
    val address: Address
)