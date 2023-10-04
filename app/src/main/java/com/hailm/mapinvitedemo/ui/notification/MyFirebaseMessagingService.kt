package com.hailm.mapinvitedemo.ui.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.hailm.mapinvitedemo.R
import com.hailm.mapinvitedemo.base.extension.printLog

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        if (remoteMessage.data.isNotEmpty()) {
            // Xử lý dữ liệu thông báo ở đây. Ví dụ:
            val title = remoteMessage.data["title"]
            val message = remoteMessage.data["body"]
            showNotification(title.toString(), message.toString())
        }

        remoteMessage.notification?.let {
            printLog(" From Message Notification Body: ${it.body}")
            // Xử lý thông báo ở đây. Ví dụ: Hiển thị thông báo trên thanh thông báo.
        }
    }

    private fun showNotification(title: String, message: String) {
        val channelId = "channel_id"
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        // Kiểm tra phiên bản Android và tạo kênh thông báo nếu cần.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Channel Name",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        // Hiển thị thông báo.
        with(NotificationManagerCompat.from(this)) {
            if (ActivityCompat.checkSelfPermission(
                    this@MyFirebaseMessagingService,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            notify(0, notificationBuilder.build())
        }
    }

    override fun onNewToken(token: String) {
        printLog("Refreshed token: $token")
    }

    companion object {
        private const val TAG = "MyFirebaseMsgService"
    }
}
