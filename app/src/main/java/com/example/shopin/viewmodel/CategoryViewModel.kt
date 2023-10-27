package com.example.shopin.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shopin.data.Category
import com.example.shopin.data.Product
import com.example.shopin.utils.Resource
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CategoryViewModel constructor(
    private val firestore: FirebaseFirestore,
    private val category: Category
):ViewModel() {

    private val _offerProducts = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val offerProducts = _offerProducts.asStateFlow()

    private val _bestProducts = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val bestProducts = _bestProducts.asStateFlow()

    private val pagingGridInfo =PagingGridInfo()
    private val pagingLinearInfo =PagingLinearInfo()
    init {
        fetchOfferProducts()
        fetchBestProducts()
    }
    fun fetchOfferProducts(){
        if(!pagingLinearInfo.isPagingEnd) {
            viewModelScope.launch {
                _offerProducts.emit(Resource.Loading())
            }
            firestore.collection("Products")
                .whereEqualTo("category", category.category)
                .whereNotEqualTo("offerPercentage", null)
                .limit(pagingLinearInfo.page*5).get()
                .addOnSuccessListener {
                    val products = it.toObjects(Product::class.java)
                    pagingLinearInfo.isPagingEnd = pagingLinearInfo.oldProducts==products
                    pagingLinearInfo.oldProducts=products
                    viewModelScope.launch {
                        _offerProducts.emit(Resource.Success(products))
                    }
                    pagingLinearInfo.page++
                }.addOnFailureListener {
                    viewModelScope.launch {
                        _offerProducts.emit(Resource.Error(it.message.toString()))
                    }
                }
        }
    }

    fun fetchBestProducts(){
        if(!pagingGridInfo.isPagingEnd) {

            viewModelScope.launch {
                _bestProducts.emit(Resource.Loading())
            }
            firestore.collection("Products")
                .whereEqualTo("category", category.category)
                .whereEqualTo("offerPercentage", null)
                .limit(pagingGridInfo.bestProductPage*10).get()
                .addOnSuccessListener {
                    val products = it.toObjects(Product::class.java)
                    pagingGridInfo.isPagingEnd= pagingGridInfo.oldBestProducts==products
                    pagingGridInfo.oldBestProducts= products
                    viewModelScope.launch {
                        _bestProducts.emit(Resource.Success(products))
                    }
                    pagingGridInfo.bestProductPage++
                }.addOnFailureListener {
                    viewModelScope.launch {
                        _bestProducts.emit(Resource.Error(it.message.toString()))
                    }
                }
        }
    }
}
