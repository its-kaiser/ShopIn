package com.example.shopin.viewmodel

import android.location.Address
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AddressViewModel @Inject constructor(
    private val firestore: FirebaseFirestore
):ViewModel() {

    fun addAddress(address: Address){

    }
}