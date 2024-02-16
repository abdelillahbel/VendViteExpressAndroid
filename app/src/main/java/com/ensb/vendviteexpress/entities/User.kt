package com.ensb.vendviteexpress.entities

import com.google.firebase.firestore.GeoPoint

data class User(
    val name: String,
    val email: String,
    val phoneNumber: String? = null,
    val type: String,
    val location: GeoPoint?,
    val uid: String? = null // Firebase assigns the user ID on auth creation
)
