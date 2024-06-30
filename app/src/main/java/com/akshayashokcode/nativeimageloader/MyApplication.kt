package com.akshayashokcode.nativeimageloader

import android.app.Application

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        ImageLoader.initialize(this)
    }
}
