package com.ensb.vendviteexpress.view

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.ensb.vendviteexpress.R
import com.ensb.vendviteexpress.utils.Utils.startAuthActivity
import com.ensb.vendviteexpress.utils.Utils.startNewActivity
import com.ensb.vendviteexpress.view.ui.auth.AuthActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        setTheme(R.style.Theme_VendViteExpress)

        auth = Firebase.auth

        checkIfUserIsLoggedIn()

    }

    private fun checkIfUserIsLoggedIn() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            // User is already signed in - Navigate to Home screen
            startNewActivity(MainActivity::class.java)
        } else {
            // No user signed in - Navigate to Login/Signup screen
            startAuthActivity()
            finish()
        }
    }
}