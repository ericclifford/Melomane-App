package com.primefour.melomane

import android.app.Application
import android.content.Intent

class App : Application() {
    companion object {
        lateinit var instance: App private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}