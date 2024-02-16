package com.ensb.vendviteexpress.view.ui.seller.account

import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

class SellerAccountViewModel : ViewModel() {

    fun logout() {
        Firebase.auth.signOut()
    }
}