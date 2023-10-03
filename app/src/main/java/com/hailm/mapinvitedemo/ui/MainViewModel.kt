package com.hailm.mapinvitedemo.ui

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.FirebaseFirestore
import com.hailm.mapinvitedemo.base.BaseViewModel
import com.hailm.mapinvitedemo.base.cache.UserProfileProvider
import com.hailm.mapinvitedemo.base.extension.isInsideGeofence
import com.hailm.mapinvitedemo.base.extension.printLog
import com.hailm.mapinvitedemo.base.model.ZoneAlert
import com.hailm.mapinvitedemo.base.util.Constants
import com.hailm.mapinvitedemo.base.util.Constants.GEOFENCE_RADIUS
import com.hailm.mapinvitedemo.ui.zone_alert.ZoneAlertUiModel
import com.hailm.mapinvitedemo.ui.zone_create.CreateZoneFragment
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
                                data.zoneName,
                                data.zoneLat,
                                data.zoneLong,
                                data.zoneRadius,
                                data.zonePhoneNumber,
                                data.zoneType,
                                document.id
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
                                zoneAlertUiModel.zoneName.toString(),
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
        zoneName: String,
        context: Context?
    ) {
        viewModelScope.launch {
            val zoneMember =
                firestore.collection(Constants.ZONE_MEMBER).document(zoneMemberDocumentId)
            printLog("zoneMemberDocumentId==> $zoneMemberDocumentId")
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
                                Toast.makeText(
                                    context,
                                    "Đối tượng đã vào trong vùng theo dõi ($zoneName) ",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                Toast.makeText(
                                    context,
                                    "Đối tượng đã ra khỏi trong vùng theo dõi ($zoneName)",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                        zoneMember.update("isInsideGeofence", isCheck)
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
}
