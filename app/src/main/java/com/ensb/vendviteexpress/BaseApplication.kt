package com.ensb.vendviteexpress

import android.app.Application
import androidx.lifecycle.ProcessLifecycleOwner

class BaseApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        ProcessLifecycleOwner.get().lifecycle.addObserver(AppLifecycleObserver(this))
    }
}