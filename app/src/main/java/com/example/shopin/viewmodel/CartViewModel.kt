package com.example.shopin.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shopin.data.Cart
import com.example.shopin.firebase.ExtractCommonInfo
import com.example.shopin.helper.getProductPrice
import com.example.shopin.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val fbAuth:FirebaseAuth,
    private val fbCommon:ExtractCommonInfo
):ViewModel() {

    private val _cartProducts  = MutableStateFlow<Resource<List<Cart>>>(Resource.Unspecified())
    val cartProducts = _cartProducts.asStateFlow()

    private val _deleteDialog = MutableSharedFlow<Cart>()
    val deleteDialog = _deleteDialog.asSharedFlow()

    val productPrice = cartProducts.map {
        when(it){
            is Resource.Success->{
                calculatePrice(it.data!!)
            }
            else->null
        }
    }

    private fun calculatePrice(data: List<Cart>): Float {
        return data.sumByDouble {cartProduct->
            (cartProduct.product.offerPercentage.getProductPrice(cartProduct.product.price)*cartProduct.quantity).toDouble()
        }.toFloat()
    }

    private var cartProductDocuments= emptyList<DocumentSnapshot>()
    init {
        fetchCartProducts()
    }
    private fun fetchCartProducts(){
        viewModelScope.launch {
            _cartProducts.emit(Resource.Loading())
        }

        firestore.collection("user").document(fbAuth.uid!!).collection("cart")
            .addSnapshotListener{value,e->

                if(e!=null || value ==null){
                    viewModelScope.launch {
                        _cartProducts.emit(Resource.Error(e?.message.toString()))
                    }
                }
                else{
                    cartProductDocuments=value.documents
                    val cartProducts = value.toObjects(Cart::class.java)
                    viewModelScope.launch {
                        _cartProducts.emit(Resource.Success(cartProducts))
                    }
                }
            }
    }

    fun changeQuantity(
        cart: Cart,
        quantityChanging: ExtractCommonInfo.QuantityChanging
    ){

        val index = cartProducts.value.data?.indexOf(cart)

        /*
         *index could be equal to -1 if the function [getCartProducts] delays which will also
         * delay the result which we expect to be inside the [_cartProducts] and to
         *  prevent the app from crashing we make a check
         */
        if(index!=null && index!=-1) {
            val documentId = cartProductDocuments[index].id

            when(quantityChanging){
                ExtractCommonInfo.QuantityChanging.INCREASE->{
                    viewModelScope.launch {
                        _cartProducts.emit(Resource.Loading())
                    }
                    increaseQuantity(documentId)
                }
                ExtractCommonInfo.QuantityChanging.DECREASE->{
                    if(cart.quantity==1){
                       viewModelScope.launch {
                           _deleteDialog.emit(cart)
                       }
                        return
                    }
                    viewModelScope.launch {
                        _cartProducts.emit(Resource.Loading())
                    }
                    decreaseQuantity(documentId)
                }
            }
        }
    }

    private fun decreaseQuantity(documentId: String) {
        fbCommon.decreaseQuantity(documentId){res, e->
            if(e!=null){
                viewModelScope.launch {
                    _cartProducts.emit(Resource.Error(e.message.toString()))
                }
            }

        }
    }

    private fun increaseQuantity(documentId: String) {
        fbCommon.increaseQuantity(documentId){res, e->
            if(e!=null){
                viewModelScope.launch {
                    _cartProducts.emit(Resource.Error(e.message.toString()))
                }
            }

        }
    }

    fun deleteCartProduct(cart: Cart){
        val index = cartProducts.value.data?.indexOf(cart)
        if(index!=null && index!=-1) {
            val documentId = cartProductDocuments[index].id
            firestore.collection("user").document(fbAuth.uid!!).collection("cart")
                .document(documentId).delete()
        }
    }
}