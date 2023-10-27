package com.example.shopin.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shopin.data.Cart
import com.example.shopin.firebase.ExtractCommonInfo
import com.example.shopin.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
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
    }
}