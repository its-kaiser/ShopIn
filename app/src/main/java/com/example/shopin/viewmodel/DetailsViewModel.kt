package com.example.shopin.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shopin.data.Cart
import com.example.shopin.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    val fbAuth :FirebaseAuth
) :ViewModel(){

    private val _addToCart = MutableStateFlow<Resource<Cart>>(Resource.Unspecified())
    val addToCart = _addToCart.asStateFlow()

    fun addUpdateProductInCart(cart: Cart){
        viewModelScope.launch {
            _addToCart.emit(Resource.Loading())
        }
        firestore.collection("user").document(fbAuth.uid!!)
            .collection("cart")
            .whereEqualTo("product.id",cart.product.id).get()
            .addOnSuccessListener {
                it.documents.let {
                    //add a new product
                    if(it.isEmpty()){

                    }
                    else{
                        val product= it.first().toObject(Cart::class.java)

                        //increase the quantity
                        if(product==cart){

                        }
                        //Add new product
                        else{

                        }
                    }
                }
            }.addOnFailureListener{
                viewModelScope.launch {
                    _addToCart.emit(Resource.Error(it.message.toString()))
                }
            }
    }
}