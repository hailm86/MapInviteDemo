package com.hailm.mapinvitedemo

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class MyNotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {

        Log.e("xxxxxx", "SASDASda")

        if (intent?.action == "com.your.package.name.NEW_FCM_MESSAGE") {
            val data = intent.getBundleExtra("data")

            // Process the FCM data here
            // You can start an activity or perform any other action as needed
        }

    }
}