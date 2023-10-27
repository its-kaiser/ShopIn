package com.example.shopin.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shopin.data.Cart
import com.example.shopin.firebase.ExtractCommonInfo
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
    private val fbAuth :FirebaseAuth,
    private val commonInfo:ExtractCommonInfo
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
                        addNewProduct(cart)
                    }
                    else{
                        val product= it.first().toObject(Cart::class.java)

                        //increase the quantity
                        if(product==cart){
                            val documentId = it.first().id
                            increaseQuantity(documentId,cart)
                        }
                        //Add new product
                        else{
                            addNewProduct(cart)
                        }
                    }
                }
            }.addOnFailureListener{
                viewModelScope.launch {
                    _addToCart.emit(Resource.Error(it.message.toString()))
                }
            }
    }

    private fun addNewProduct(cart:Cart){
        commonInfo.addProductToCart(cart){addedProduct,e->
            viewModelScope.launch {
                if(e==null){
                    _addToCart.emit(Resource.Success(addedProduct!!))
                }
                else{
                    _addToCart.emit(Resource.Error(e.message.toString()))
                }
            }
        }
    }

    private fun increaseQuantity(documentId:String, cart:Cart){
        commonInfo.increaseQuantity(documentId){_,e->
            viewModelScope.launch {
                if(e==null){
                    _addToCart.emit(Resource.Success(cart))
                }
                else{
                    _addToCart.emit(Resource.Error(e.message.toString()))
                }
            }
        }
    }
}