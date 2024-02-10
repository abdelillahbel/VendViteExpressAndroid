package com.ensb.vendviteexpress.entities

data class User(
    val name: String,
    val email: String,
    val uid: String? = null // Firebase assigns the user ID on auth creation
)
