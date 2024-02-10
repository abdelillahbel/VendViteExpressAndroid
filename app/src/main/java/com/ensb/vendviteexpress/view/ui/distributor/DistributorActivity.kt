package com.ensb.vendviteexpress.view.ui.distributor

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupWithNavController
import com.ensb.vendviteexpress.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class DistributorActivity : AppCompatActivity() {
    private lateinit var navController: NavController

    private lateinit var appBarConfiguration: AppBarConfiguration
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_distributor)
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.distributorBottomNavigationView)
        val navigationHost =
            supportFragmentManager.findFragmentById(R.id.distributor_nav_host_fragment) as NavHostFragment
        val navController = navigationHost.navController

        bottomNavigation.setupWithNavController(navController)


    }
    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration)
    }
}