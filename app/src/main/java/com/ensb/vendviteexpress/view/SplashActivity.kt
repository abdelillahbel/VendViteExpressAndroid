package com.ensb.vendviteexpress.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.ensb.vendviteexpress.R
import com.ensb.vendviteexpress.utils.TAG
import com.ensb.vendviteexpress.utils.Utils.startAuthActivity
import com.ensb.vendviteexpress.utils.Utils.startNewActivity
import com.ensb.vendviteexpress.view.ui.distributor.DistributorActivity
import com.ensb.vendviteexpress.view.ui.seller.SellerActivity
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



        if (auth.currentUser != null) {
            // User is signed-in, load type from Shared Preferences
            val sharedPreferences = getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
            val userType = sharedPreferences.getString("user_type", null)

            if (userType != null) {
                navigateToAppropriateActivity(userType)
            } else {
                // Handle missing type in SharedPreferences (e.g., new app instance)
                Log.e(TAG, "missing type in SharedPreferences")
            }
        } else {
            startAuthActivity()
            finish()
        }

    }

    private fun navigateToAppropriateActivity(userType: String) {
        when (userType) {
            "seller" -> startNewActivity(SellerActivity::class.java)
            "distributor" -> startNewActivity(DistributorActivity::class.java)
        }
    }
}