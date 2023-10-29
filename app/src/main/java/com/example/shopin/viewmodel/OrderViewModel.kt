package com.example.shopin.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shopin.data.Order
import com.example.shopin.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val fbAuth: FirebaseAuth
):ViewModel(){
    private val _order=MutableStateFlow<Resource<Order>>(Resource.Unspecified())
    val order = _order.asStateFlow()


    fun placeOrder(order:Order){
        viewModelScope.launch {
            _order.emit(Resource.Loading())
        }
        firestore.runBatch {batch->

            //writing in user->orders
            firestore.collection("user").document(fbAuth.uid!!).collection("orders")
                .document().set(order)

            //writing in orders-> (a separate new collection)
            firestore.collection("orders").document().set(order)

            //delete the products from the cart
            firestore.collection("user").document(fbAuth.uid!!).collection("cart")
                .get()
                .addOnSuccessListener {
                    it.documents.forEach{product->
                        product.reference.delete()
                    }
                }
        }.addOnSuccessListener {
            viewModelScope.launch {
                _order.emit(Resource.Success(order))
            }
        }.addOnFailureListener{
            viewModelScope.launch {
                _order.emit(Resource.Error(it.message.toString()))
            }
        }
    }
}