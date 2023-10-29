package com.example.shopin.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shopin.data.Address
import com.example.shopin.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BillingViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val fbAuth: FirebaseAuth
): ViewModel() {

    private val _address = MutableStateFlow<Resource<List<Address>>>(Resource.Unspecified())
    val address = _address.asStateFlow()

    init {
        fetchUserAddresses()
    }
    fun fetchUserAddresses(){
        viewModelScope.launch {
            _address.emit(Resource.Loading())
        }
        firestore.collection("user").document(fbAuth.uid!!).collection("address")
            .addSnapshotListener{value, error->
                if(error !=null){
                    viewModelScope.launch {
                        _address.emit(Resource.Error(error.message.toString()))
                    }
                    return@addSnapshotListener
                }
                val addresses = value?.toObjects(Address::class.java)

                viewModelScope.launch {
                    _address.emit(Resource.Success(addresses!!))
                }
            }
    }
}