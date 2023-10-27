package com.example.shopin.firebase

import com.example.shopin.data.Cart
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ExtractCommonInfo(
    private val firestore: FirebaseFirestore,
    private val fbAuth: FirebaseAuth
) {

    private val cartCollection = firestore.collection("user")
        .document(fbAuth.uid!!).collection("cart")

    fun addProductToCart(cart: Cart,onResult:(Cart?,Exception?)->Unit){
        cartCollection.document().set(cart)
            .addOnSuccessListener {
                onResult(cart,null)
            }.addOnFailureListener{
                onResult(null,it)
            }
    }

    fun increaseQuantity(documentId: String, onResult: (String?, Exception?) -> Unit){
       firestore.runTransaction {transaction->

           val documentRef = cartCollection.document(documentId)
           val document = transaction.get(documentRef)
           val productObject  = document.toObject(Cart::class.java)

           productObject?.let {cartProduct->
               val newQuantity = cartProduct.quantity+1
               val newProductObject = cartProduct.copy(quantity=newQuantity)
               transaction.set(documentRef,newProductObject)
           }
       }.addOnSuccessListener {
           onResult(documentId,null)
       }.addOnFailureListener{
           onResult(null, it)
       }
    }
}