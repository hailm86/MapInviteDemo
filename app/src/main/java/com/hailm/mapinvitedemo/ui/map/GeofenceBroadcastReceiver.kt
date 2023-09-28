package com.hailm.mapinvitedemo.ui.map

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingEvent
import com.hailm.mapinvitedemo.base.extension.printLog

class GeofenceBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent) {
        printLog("intent ==> ${intent.action}")

        val geofencingEvent = GeofencingEvent.fromIntent(intent)
        if (geofencingEvent != null) {
            if (geofencingEvent.hasError()) {
                val errorMessage = GeofenceStatusCodes
                    .getStatusCodeString(geofencingEvent.errorCode)
                printLog(errorMessage)
                return
            }
        }

        // Get the transition type.
        val geofenceTransition = geofencingEvent?.geofenceTransition

        // Test that the reported transition was of interest.
        when (geofenceTransition) {
            Geofence.GEOFENCE_TRANSITION_ENTER -> {
                // Người dùng đã đi vào khu vực địa lý
                printLog("Geofence User entered geofence")
                // Thực hiện các hành động bạn muốn ở đây khi người dùng đi vào khu vực
            }

            Geofence.GEOFENCE_TRANSITION_EXIT -> {
                // Người dùng đã ra khỏi khu vực địa lý
                printLog("Geofence User exited geofence")
                // Thực hiện các hành động bạn muốn ở đây khi người dùng ra khỏi khu vực
            }

            else -> {
                // Không có sự kiện geofence phù hợp
            }
        }
    }
}