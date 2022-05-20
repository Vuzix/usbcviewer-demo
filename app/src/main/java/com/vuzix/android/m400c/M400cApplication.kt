package com.vuzix.android.m400c

import android.app.Application
import timber.log.Timber

class M400cApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}