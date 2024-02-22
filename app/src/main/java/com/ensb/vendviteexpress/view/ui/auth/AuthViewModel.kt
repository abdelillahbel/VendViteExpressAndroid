package com.ensb.vendviteexpress.view.ui.auth

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ensb.vendviteexpress.entities.User
import com.ensb.vendviteexpress.utils.Response
import com.ensb.vendviteexpress.utils.USERS
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


class AuthViewModel : ViewModel() {

    private val auth: FirebaseAuth = Firebase.auth
    private val db = Firebase.firestore

    private val _authState = MutableLiveData<Response<Boolean>>() // Updated type
    val authState: LiveData<Response<Boolean>> = _authState


    fun registerUser(
        name: String,
        email: String,
        phoneNumber: String?,
        type: String,
        location: GeoPoint?,
        password: String
    ) {
        _authState.value = Response.Loading
        viewModelScope.launch {
            try {
                val result = auth.createUserWithEmailAndPassword(email, password).await()
                val user = User(name, email, phoneNumber, type, location, result.user?.uid)
                storeUserData(user)
                _authState.value = Response.Success(true) // Signup successful
            } catch (e: Exception) {
                _authState.value = Response.Failure(e)
            }
        }
    }

    fun loginUser(email: String, password: String) {
        _authState.value = Response.Loading
        viewModelScope.launch {
            try {
                auth.signInWithEmailAndPassword(email, password).await()
                _authState.value = Response.Success(true)
            } catch (e: Exception) {
                _authState.value = Response.Failure(e)
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
                    // todo
                    Log.e("storeUserData: ", e.message.toString())
                }
            }
        }
    }

    suspend fun getUserName(): String? {
        val db = Firebase.firestore
        val userId = Firebase.auth.currentUser?.uid

        return try {
            if (userId != null) {
                val userDocRef = db.collection("users").document(userId)
                val snapshot = userDocRef.get().await()
                snapshot.getString("name")
            } else {
                null
            }
        } catch (e: Exception) {
            // todo
            null
        }
    }

    private fun logout() = auth.signOut()

    private fun currentUser() = auth.currentUser


}