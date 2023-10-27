package com.example.shopin.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shopin.data.Product
import com.example.shopin.utils.Resource
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainCategoryViewModel @Inject constructor(
    private val firestore:FirebaseFirestore
):ViewModel() {

    private val _specialProducts  = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val specialProducts: StateFlow<Resource<List<Product>>> = _specialProducts

    private val _bestDealsProducts  = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val bestDealsProducts: StateFlow<Resource<List<Product>>> = _bestDealsProducts

    private val _bestProducts  = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val bestProducts: StateFlow<Resource<List<Product>>> = _bestProducts

    private val pagingGridInfo =PagingGridInfo()
    private val pagingLinearInfo =PagingLinearInfo()

    init {
        fetchSpecialProducts()
        fetchBestDeals()
        fetchBestProducts()
    }
    fun fetchSpecialProducts(){
        if(!pagingLinearInfo.isPagingEnd) {
            viewModelScope.launch {
                _specialProducts.emit(Resource.Loading())
            }
            firestore.collection("Products").whereEqualTo("category", "Special Products")
                .limit(pagingLinearInfo.page*5).get()
                .addOnSuccessListener { res ->
                    val specialProductList = res.toObjects(Product::class.java)
                    pagingLinearInfo.isPagingEnd = specialProductList == pagingLinearInfo.oldProducts
                    pagingLinearInfo.oldProducts = specialProductList
                    viewModelScope.launch {
                        _specialProducts.emit(Resource.Success(specialProductList))
                    }
                    pagingLinearInfo.page++
                }.addOnFailureListener {
                    viewModelScope.launch {
                        _specialProducts.emit(Resource.Error(it.message.toString()))
                    }
                }
        }
    }

    fun fetchBestDeals(){
        if(!pagingLinearInfo.isPagingEnd) {
            viewModelScope.launch {
                _bestDealsProducts.emit(Resource.Loading())
            }
            firestore.collection("Products").whereEqualTo("category", "Best Deals")
                .limit(pagingLinearInfo.page*5).get()
                .addOnSuccessListener { res ->
                    val bestDealsProductList = res.toObjects(Product::class.java)
                    pagingLinearInfo.isPagingEnd = bestDealsProductList==pagingLinearInfo.oldProducts
                    pagingLinearInfo.oldProducts = bestDealsProductList
                    viewModelScope.launch {
                        _bestDealsProducts.emit(Resource.Success(bestDealsProductList))
                    }
                    pagingLinearInfo.page++
                }.addOnFailureListener {
                    viewModelScope.launch {
                        _bestDealsProducts.emit(Resource.Error(it.message.toString()))
                    }
                }
        }
    }

    fun fetchBestProducts(){
        if(!pagingGridInfo.isPagingEnd) {
            viewModelScope.launch {
                _bestProducts.emit(Resource.Loading())
            }
            firestore.collection("Products").limit(pagingGridInfo.bestProductPage * 10).get()
                .addOnSuccessListener { res ->
                    val bestProductList = res.toObjects(Product::class.java)
                    pagingGridInfo.isPagingEnd = bestProductList == pagingGridInfo.oldBestProducts
                    pagingGridInfo.oldBestProducts = bestProductList
                    viewModelScope.launch {
                        _bestProducts.emit(Resource.Success(bestProductList))
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

internal data class PagingGridInfo(
    var bestProductPage :Long =1,
    var oldBestProducts :List<Product> = emptyList(),
    var isPagingEnd :Boolean = false
)

internal data class PagingLinearInfo(
    var page :Long =1,
    var oldProducts :List<Product> = emptyList(),
    var isPagingEnd: Boolean = false
)