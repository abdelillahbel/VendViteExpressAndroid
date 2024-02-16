package com.ensb.vendviteexpress

import android.app.Application
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.ensb.vendviteexpress.utils.USERS
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore

class AppLifecycleObserver(application: Application) : DefaultLifecycleObserver {

    private val db = Firebase.firestore
    private val userId = Firebase.auth.currentUser?.uid

    override fun onResume(owner: LifecycleOwner) {
        updateUserActiveStatus(true)
    }

    override fun onPause(owner: LifecycleOwner) {
        updateUserActiveStatus(false)
    }

    private fun updateUserActiveStatus(isActive: Boolean) {
        if (userId != null) {
            val userRef = db.collection(USERS).document(userId)
            userRef.update("active", isActive)
        }
    }
}