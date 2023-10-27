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

    private val pagingInfo =PagingInfo()
    private val pagingInfoAgain =PagingInfoAgain()

    init {
        fetchSpecialProducts()
        fetchBestDeals()
        fetchBestProducts()
    }
    fun fetchSpecialProducts(){
        if(!pagingInfoAgain.isPagingEnd) {
            viewModelScope.launch {
                _specialProducts.emit(Resource.Loading())
            }
            firestore.collection("Products").whereEqualTo("category", "Special Products")
                .limit(pagingInfoAgain.page*5).get()
                .addOnSuccessListener { res ->
                    val specialProductList = res.toObjects(Product::class.java)
                    pagingInfoAgain.isPagingEnd = specialProductList == pagingInfoAgain.oldProducts
                    pagingInfoAgain.oldProducts = specialProductList
                    viewModelScope.launch {
                        _specialProducts.emit(Resource.Success(specialProductList))
                    }
                    pagingInfoAgain.page++
                }.addOnFailureListener {
                    viewModelScope.launch {
                        _specialProducts.emit(Resource.Error(it.message.toString()))
                    }
                }
        }
    }

    fun fetchBestDeals(){
        if(!pagingInfoAgain.isPagingEnd) {
            viewModelScope.launch {
                _bestDealsProducts.emit(Resource.Loading())
            }
            firestore.collection("Products").whereEqualTo("category", "Best Deals")
                .limit(pagingInfoAgain.page*5).get()
                .addOnSuccessListener { res ->
                    val bestDealsProductList = res.toObjects(Product::class.java)
                    pagingInfoAgain.isPagingEnd = bestDealsProductList==pagingInfoAgain.oldProducts
                    pagingInfoAgain.oldProducts = bestDealsProductList
                    viewModelScope.launch {
                        _bestDealsProducts.emit(Resource.Success(bestDealsProductList))
                    }
                    pagingInfoAgain.page++
                }.addOnFailureListener {
                    viewModelScope.launch {
                        _bestDealsProducts.emit(Resource.Error(it.message.toString()))
                    }
                }
        }
    }

    fun fetchBestProducts(){
        if(!pagingInfo.isPagingEnd) {
            viewModelScope.launch {
                _bestProducts.emit(Resource.Loading())
            }
            firestore.collection("Products").limit(pagingInfo.bestProductPage * 10).get()
                .addOnSuccessListener { res ->
                    val bestProductList = res.toObjects(Product::class.java)
                    pagingInfo.isPagingEnd = bestProductList == pagingInfo.oldBestProducts
                    pagingInfo.oldBestProducts = bestProductList
                    viewModelScope.launch {
                        _bestProducts.emit(Resource.Success(bestProductList))
                    }
                    pagingInfo.bestProductPage++
                }.addOnFailureListener {
                    viewModelScope.launch {
                        _bestProducts.emit(Resource.Error(it.message.toString()))
                    }
                }
        }
    }
}

internal data class PagingInfo(
    var bestProductPage :Long =1,
    var oldBestProducts :List<Product> = emptyList(),
    var isPagingEnd :Boolean = false
)

internal data class PagingInfoAgain(
    var page :Long =1,
    var oldProducts :List<Product> = emptyList(),
    var isPagingEnd: Boolean = false
)