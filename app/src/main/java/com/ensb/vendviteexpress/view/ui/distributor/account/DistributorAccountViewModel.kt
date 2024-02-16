package com.ensb.vendviteexpress.view.ui.distributor.account

import android.app.Activity
import android.content.Intent
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class DistributorAccountViewModel : ViewModel() {


    fun logout() {
        Firebase.auth.signOut()
    }
}