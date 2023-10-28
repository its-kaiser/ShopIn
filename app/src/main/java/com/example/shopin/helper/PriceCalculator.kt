package com.example.shopin.helper

fun Float?.getProductPrice(price:Float):Float{
    //here this--> percentage
    if(this==null){
        return price
    }

    val remainingPricePerc = 1f-this
    val priceAfterOffer = remainingPricePerc*price

    return priceAfterOffer
}