package com.example.shopin.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shopin.data.order.Order
import com.example.shopin.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AllOrdersViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val fbAuth : FirebaseAuth
):ViewModel() {

    private val _allOrders = MutableStateFlow<Resource<List<Order>>>(Resource.Unspecified())
    val allOrders = _allOrders.asStateFlow()


    fun fetchAllOrders(){
        viewModelScope.launch {
            _allOrders.emit(Resource.Loading())
        }

        firestore.collection("user").document(fbAuth.uid!!).collection("orders").get()
            .addOnSuccessListener {

                val orders = it.toObjects(Order::class.java)
                viewModelScope.launch {
                    _allOrders.emit(Resource.Success(orders))
                }
            }.addOnFailureListener{
                viewModelScope.launch {
                    _allOrders.emit(Resource.Error(it.message.toString()))
                }
            }
    }
}