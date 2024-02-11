package com.ensb.vendviteexpress.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.util.Patterns
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import com.ensb.vendviteexpress.view.ui.seller.SellerActivity
import com.ensb.vendviteexpress.view.ui.auth.AuthActivity

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
        Intent(this, SellerActivity::class.java).also {
            it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(it)
        }

    fun Context.startAuthActivity() =
        Intent(this, AuthActivity::class.java).also {
            it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(it)
        }

    fun String.isEmailValid(): Boolean {
        return !TextUtils.isEmpty(this) && Patterns.EMAIL_ADDRESS.matcher(this).matches()
    }

}



