package com.ensb.vendviteexpress.view

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ensb.vendviteexpress.R
import com.ensb.vendviteexpress.utils.Utils.startNewActivity

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        setTheme(R.style.Theme_VendViteExpress)
        startNewActivity(MainActivity::class.java)

    }
}