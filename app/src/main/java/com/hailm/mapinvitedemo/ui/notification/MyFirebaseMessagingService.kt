package com.hailm.mapinvitedemo.ui.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.hailm.mapinvitedemo.MainActivity
import com.hailm.mapinvitedemo.R
import com.hailm.mapinvitedemo.base.cache.UserProfileProvider
import com.hailm.mapinvitedemo.base.extension.printLog
import com.hailm.mapinvitedemo.local.DataRepo
import com.hailm.mapinvitedemo.local.entity.NotificationEntity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MyFirebaseMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var dataRepo: DataRepo

    @Inject
    lateinit var userProfileProvider: UserProfileProvider

    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        printLog("onMessageReceived ======> ${remoteMessage.data["body"]}")

        if (remoteMessage.data.isNotEmpty()) {
            val title = remoteMessage.data["title"]
            val message = remoteMessage.data["body"]
            val zoneName = remoteMessage.data["zoneName"]
            val updateDate = remoteMessage.data["updateDate"]
            // save to db
            GlobalScope.launch(Dispatchers.IO) {
                dataRepo.insertNotification(
                    NotificationEntity(
                        phoneNumber = userProfileProvider.userPhoneNumber.toString(),
                        title = title.toString(),
                        body = message.toString(),
                        zoneName = zoneName.toString(),
                        createTime = updateDate.toString()
                    )
                )
            }

            showNotification(title.toString(), message.toString())
        }
    }

    private fun showNotification(title: String, message: String) {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val channelId = "channel_id"
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)

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
        val notificationId = System.currentTimeMillis().toInt()
        with(NotificationManagerCompat.from(this)) {
            if (ActivityCompat.checkSelfPermission(
                    this@MyFirebaseMessagingService,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            notify(notificationId, notificationBuilder.build())
        }
    }

    override fun onNewToken(token: String) {
        printLog("Refreshed token: $token")
    }

    companion object {
        private const val TAG = "MyFirebaseMsgService"
    }
}
