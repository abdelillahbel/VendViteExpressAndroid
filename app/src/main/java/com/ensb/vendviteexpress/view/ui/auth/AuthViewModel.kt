package com.ensb.vendviteexpress.view.ui.auth

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ensb.vendviteexpress.entities.User
import com.ensb.vendviteexpress.utils.Response
import com.ensb.vendviteexpress.utils.USERS
import com.ensb.vendviteexpress.view.MainActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AuthViewModel : ViewModel() {

    private val auth: FirebaseAuth = Firebase.auth
    private val db = Firebase.firestore

    private val _authResult = MutableLiveData<Response<Boolean>>() // Updated type
    val authResult: LiveData<Response<Boolean>> = _authResult


    fun registerUser(name: String, email: String, password: String) {
        viewModelScope.launch {
            try {
                val result = auth.createUserWithEmailAndPassword(email, password).await()
                val user = User(name, email, result.user?.uid)
                storeUserData(user)
                _authResult.value = Response.Success(true) // Signup successful
            } catch (e: Exception) {
                _authResult.value = Response.Failure(e)
            }
        }
    }

    fun loginUser(email: String, password: String) {
        viewModelScope.launch {
            try {
                auth.signInWithEmailAndPassword(email, password).await()
                _authResult.value = Response.Success(true) // Login successful
            } catch (e: Exception) {
                _authResult.value = Response.Failure(e)
            }
        }
    }

    private fun storeUserData(user: User) {
        viewModelScope.launch {
            val usersCollection = db.collection(USERS)
            user.uid?.let { uid ->
                try {
                    usersCollection.document(uid).set(user).await()
                } catch (e: Exception) {
                    // Handle Firestore errors appropriately
                }
            }
        }
    }

}