package com.hailm.mapinvitedemo.ui

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.hailm.mapinvitedemo.R
import com.hailm.mapinvitedemo.base.BaseViewModel
import com.hailm.mapinvitedemo.base.cache.UserProfileProvider
import com.hailm.mapinvitedemo.base.extension.getCreatedDate
import com.hailm.mapinvitedemo.base.extension.isInsideGeofence
import com.hailm.mapinvitedemo.base.extension.printLog
import com.hailm.mapinvitedemo.base.model.ZoneAlert
import com.hailm.mapinvitedemo.base.util.Constants
import com.hailm.mapinvitedemo.base.util.Constants.GEOFENCE_RADIUS
import com.hailm.mapinvitedemo.ui.notification.NotificationUtils
import com.hailm.mapinvitedemo.ui.zone_alert.ZoneAlertUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val userProfileProvider: UserProfileProvider
) : BaseViewModel() {


    fun getListZone(currentLatLng: LatLng, context: Context?) {
        viewModelScope.launch {
            val currentPhone = userProfileProvider.userPhoneNumber

            val zoneAlert = firestore.collection(Constants.ZONE_ALERT)
            zoneAlert.whereArrayContains("zoneMember", currentPhone.toString())
                .get()
                .addOnSuccessListener { documents ->
                    if (!documents.isEmpty) {
                        val zoneAlertUiModels: MutableList<ZoneAlertUiModel> = mutableListOf()
                        for (document in documents) {
                            val data = document.toObject(ZoneAlert::class.java)
                            val zoneAlertUiModel = ZoneAlertUiModel(
                                zoneName = data.zoneName,
                                zoneLat = data.zoneLat,
                                zoneLong = data.zoneLong,
                                zoneRadius = data.zoneRadius,
                                zonePhoneNumber = data.zonePhoneNumber,
                                zoneType = data.zoneType,
                                documentId = document.id,
                                currentZoom = data.currentZoom,
                                zoneDeviceToken = data.zoneDeviceToken
                            )
                            zoneAlertUiModels.add(zoneAlertUiModel)
                        }
                        getZoneMemberId(zoneAlertUiModels, currentLatLng, context)
                    }
                }
                .addOnFailureListener { exception ->
                    printLog("Error getting documents: $exception")
                }
        }
    }

    private fun getZoneMemberId(
        zoneAlertUiModels: List<ZoneAlertUiModel>,
        currentLatLng: LatLng,
        context: Context?
    ) {
        for (zoneAlertUiModel in zoneAlertUiModels) {
            val zoneMember = firestore.collection(Constants.ZONE_MEMBER)
            zoneMember
                .whereEqualTo("documentIdZoneAlert", zoneAlertUiModel.documentId)
                .whereEqualTo("zoneMember", userProfileProvider.userPhoneNumber.toString())
                .get()
                .addOnSuccessListener { documents ->
                    if (!documents.isEmpty) {
                        for (document in documents) {
                            updateInOutZoneMember(
                                document.id,
                                currentLatLng,
                                LatLng(
                                    zoneAlertUiModel.zoneLat.toString().toDouble(),
                                    zoneAlertUiModel.zoneLong.toString().toDouble()
                                ),
                                zoneAlertUiModel,
                                context
                            )
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    printLog("Error getting documents: $exception")
                }
        }
    }

    private fun updateInOutZoneMember(
        zoneMemberDocumentId: String,
        currentLatLng: LatLng,
        latLngZone: LatLng,
        zoneAlertUiModel: ZoneAlertUiModel,
        context: Context?
    ) {
        viewModelScope.launch {
            val zoneMember =
                firestore.collection(Constants.ZONE_MEMBER).document(zoneMemberDocumentId)
            zoneMember.get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        printLog("currentLatLng => ${currentLatLng.latitude} -- ${currentLatLng.longitude}")
                        printLog("latLngZone => ${latLngZone.latitude} -- ${latLngZone.longitude}")

                        val isInsideGeofenceOld = documentSnapshot.get("isInsideGeofence")
                        val isInside = isInsideGeofence(currentLatLng, latLngZone, GEOFENCE_RADIUS)

                        val isInsideGeofenceNew = if (isInside) {
                            Constants.INSIDE
                        } else {
                            Constants.OUTSIDE
                        }

                        val isCheck: String
                        if (isInsideGeofenceOld == isInsideGeofenceNew) {
                            isCheck = isInsideGeofenceOld.toString()
                        } else {
                            isCheck = isInsideGeofenceNew
                            if (isCheck == Constants.INSIDE) {
                                printLog("Đối tượng đã vào trong vùng theo dõi (${zoneAlertUiModel.zoneName} ---  $isCheck")
                                handleInside(context, zoneAlertUiModel)
                            } else {
                                printLog("Đối tượng đã ra khỏi trong vùng theo dõi (${zoneAlertUiModel.zoneName} ---  $isCheck")
                                handleOutside(context, zoneAlertUiModel)
                            }
                        }

                        val dateTime = Timestamp.now()
                        val updates = hashMapOf(
                            "isInsideGeofence" to isCheck,
                            "updateTime" to dateTime
                        )

                        zoneMember.update(updates as Map<String, Any>)
                            .addOnSuccessListener {
                                printLog("update isInsideGeofence success $isCheck")
                                // Người dùng đã được thêm vào khu vực (Area) thành công
                            }
                            .addOnFailureListener { e ->
                                // Xử lý lỗi nếu có
                            }
                    }
                }
                .addOnFailureListener { e ->
                    // Xử lý lỗi nếu có
                }

        }
    }

    private fun handleInside(context: Context?, zoneAlertUiModel: ZoneAlertUiModel) {
        viewModelScope.launch {
            val message = "Đối tượng đã vào trong vùng theo dõi (${zoneAlertUiModel.zoneName})"
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            showNotification(context!!, "Test", message)

            val updateDate = getCreatedDate()
            NotificationUtils.sendNotificationToDevice(
                zoneAlertUiModel.zoneDeviceToken,
                "Checking location",
                message,
                zoneAlertUiModel.zoneName.toString(),
                updateDate
            )
        }
    }

    private fun handleOutside(context: Context?, zoneAlertUiModel: ZoneAlertUiModel) {
        viewModelScope.launch {
            val message = "Đối tượng đã ra khỏi trong vùng theo dõi (${zoneAlertUiModel.zoneName})"
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            showNotification(context!!, "Test", message)

            val updateDate = getCreatedDate()
            NotificationUtils.sendNotificationToDevice(
                zoneAlertUiModel.zoneDeviceToken,
                "Checking location",
                message,
                zoneAlertUiModel.zoneName.toString(),
                updateDate
            )
        }
    }

    private fun showNotification(context: Context, title: String, message: String) {
        val notificationManager = NotificationManagerCompat.from(context)

        // Kiểm tra phiên bản Android, vì từ Android 8.0 trở lên cần tạo Notification Channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "my_channel_id"
            val channelName = "My Channel"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, channelName, importance)

            notificationManager.createNotificationChannel(channel)
        }

        // Tạo một NotificationCompat.Builder để xây dựng thông báo
        val builder = NotificationCompat.Builder(context, "my_channel_id")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        // Hiển thị thông báo bằng cách gọi notify() trên NotificationManagerCompat
        val notificationId = 1 // ID duy nhất cho mỗi thông báo
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        notificationManager.notify(notificationId, builder.build())
    }
}
