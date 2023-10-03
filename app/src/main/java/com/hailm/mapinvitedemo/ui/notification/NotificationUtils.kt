package com.hailm.mapinvitedemo.ui.notification

import com.hailm.mapinvitedemo.base.extension.printLog
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody

object NotificationUtils {
    fun sendNotificationToDevice(deviceToken: String, title: String, message: String) {
        val serverKey = "YOUR_SERVER_KEY" // Thay thế bằng Server Key của bạn

        val client = OkHttpClient()
        val json = """
        {
            "to": "$deviceToken",
            "notification": {
                "title": "$title",
                "body": "$message"
            }
        }
    """.trimIndent()

        val body = RequestBody.create("application/json".toMediaTypeOrNull(), json)
        val request = Request.Builder()
            .url("https://fcm.googleapis.com/fcm/send")
            .post(body)
            .addHeader("Authorization", "key=$serverKey")
            .addHeader("Content-Type", "application/json")
            .build()

        val response = client.newCall(request).execute()
        val responseBody = response.body?.string()
        printLog("NotificationResponse $responseBody")
    }
}