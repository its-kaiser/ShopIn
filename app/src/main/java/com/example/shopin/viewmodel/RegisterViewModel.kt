package com.example.shopin.viewmodel

import androidx.lifecycle.ViewModel
import com.example.shopin.data.User
import com.example.shopin.utils.Constants.USER_COLLECTION
import com.example.shopin.utils.RegisterFieldState
import com.example.shopin.utils.RegisterValidation
import com.example.shopin.utils.Resource
import com.example.shopin.utils.validateEmail
import com.example.shopin.utils.validatePassword
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val fbAuth: FirebaseAuth,
    private val db : FirebaseFirestore
):ViewModel(){

    private val _register=MutableStateFlow<Resource<User>>(Resource.Unspecified())
    val register :Flow<Resource<User>> = _register

    private val _validation= Channel<RegisterFieldState>()

    val validation= _validation.receiveAsFlow()
    fun createAccountWithEmail(user: User, password:String){

        if(checkValidation(user, password)) {
            runBlocking {
                _register.emit(Resource.Loading())
            }
            fbAuth.createUserWithEmailAndPassword(user.email, password)
                .addOnSuccessListener {
                    it.user?.let {
                        saveUserToDatabase(it.uid, user)
                    }
                }.addOnFailureListener {
                    _register.value = Resource.Error(it.message.toString())
                }
        }
        else{
            val registerFieldState = RegisterFieldState(
                validateEmail(user.email),validatePassword(password)
            )
            runBlocking {
                _validation.send(registerFieldState)
            }
        }
    }

    //saving user's info to database
    private fun saveUserToDatabase(userUid: String, user:User) {
        db.collection(USER_COLLECTION)
            .document(userUid)
            .set(user)
            .addOnSuccessListener {
                _register.value = Resource.Success(user)
            }.addOnFailureListener{
                _register.value = Resource.Error(it.message.toString())
            }
    }

    private fun checkValidation(user: User, password: String):Boolean {
        val emailValidation = validateEmail(user.email)
        val passwordValidation = validatePassword(password)

        val shouldRegister = emailValidation is RegisterValidation.Success &&
                passwordValidation is RegisterValidation.Success

        return shouldRegister
    }
}