package com.hailm.mapinvitedemo.ui.notification

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // Xử lý thông báo nhận được tại đây.
        Log.d(TAG, "From: ${remoteMessage.from}")

        // Kiểm tra nếu thông báo có dữ liệu.
        if (remoteMessage.data.isNotEmpty()) {
            Log.d(TAG, "Message data payload: " + remoteMessage.data)

            // Xử lý dữ liệu thông báo ở đây. Ví dụ:
            val title = remoteMessage.data["title"]
            val message = remoteMessage.data["message"]
            // Hiển thị thông báo hoặc thực hiện các tác vụ khác dựa trên dữ liệu này.
        }

        // Kiểm tra nếu thông báo có thông điệp.
        remoteMessage.notification?.let {
            Log.d(TAG, "Message Notification Body: ${it.body}")

            // Xử lý thông báo ở đây. Ví dụ: Hiển thị thông báo trên thanh thông báo.
        }
    }

    override fun onNewToken(token: String) {
        // Khi token mới được tạo hoặc cập nhật, bạn có thể thực hiện các tác vụ liên quan đến token ở đây.
        Log.d(TAG, "Refreshed token: $token")
    }

    companion object {
        private const val TAG = "MyFirebaseMsgService"
    }
}
