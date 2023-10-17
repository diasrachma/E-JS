package com.example.ejs

import android.app.Application
import com.example.ejs.NotificationHelper

class BaseApplication : Application() {

    companion object {
        lateinit var notificationHelper: NotificationHelper
    }

    override fun onCreate() {
        super.onCreate()
        notificationHelper = NotificationHelper(applicationContext)
    }

    fun showBuktiNotification() {
        notificationHelper.showBuktiNotification()
    }

    fun showRatingNotification() {
        notificationHelper.showRatingNotification()
    }
}

