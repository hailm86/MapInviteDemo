package com.hailm.mapinvitedemo.ui.notification

import com.hailm.mapinvitedemo.base.extension.printLog
import com.hailm.mapinvitedemo.base.util.Constants
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

object NotificationUtils {
    @OptIn(DelicateCoroutinesApi::class)
    fun sendNotificationToDevice(
        deviceToken: String,
        title: String,
        message: String,
        zoneName: String,
        updateTime: String
    ) {
        GlobalScope.launch(Dispatchers.IO) {
            val serverKey = Constants.SERVER_KEY_FCM

            val client = OkHttpClient()
            val json = """
        {
             "to" : "$deviceToken",
             "collapse_key" : "type_a",
             "data" : {
                 "title": "$title",
                 "body" : "$message",
                 "zoneName" : "$zoneName",
                 "updateDate" : "$updateTime"
             }
        }
    """.trimIndent()

            val body = json.toRequestBody("application/json".toMediaTypeOrNull())
            val request = Request.Builder()
                .url("https://fcm.googleapis.com/fcm/send")
                .post(body)
                .addHeader("Authorization", "key=$serverKey")
                .addHeader("Content-Type", "application/json")
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string()
            // Xử lý phản hồi từ FCM (responseBody) nếu cần.
            printLog("===> responseBody $responseBody")
        }
    }
}