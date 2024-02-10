package com.ensb.vendviteexpress.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import com.ensb.vendviteexpress.R
import com.ensb.vendviteexpress.view.MainActivity
import com.ensb.vendviteexpress.view.ui.auth.AuthActivity
import com.ensb.vendviteexpress.view.ui.home.HomeFragment

object Utils {

    fun NavController.safeNavigate(direction: Any) {
        val action = if (direction is NavDirections) direction.actionId else direction
        currentDestination?.getAction(action as Int)?.run {
            navigate(action)
        }
    }

    fun NavController.safeNavigate(directions: NavDirections) {
        try {
            navigate(directions)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun <A : Activity> Activity.startNewActivity(activity: Class<A>) {
        Intent(this, activity).also {
            it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(it)
        }
    }

    fun Context.startMainActivity() =
        Intent(this, MainActivity::class.java).also {
            it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(it)
        }

    fun Context.startAuthActivity() =
        Intent(this, AuthActivity::class.java).also {
            it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(it)
        }


}



