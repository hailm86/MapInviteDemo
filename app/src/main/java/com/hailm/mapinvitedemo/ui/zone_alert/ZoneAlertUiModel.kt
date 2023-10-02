package com.hailm.mapinvitedemo.ui.zone_alert

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ZoneAlertUiModel(
    val zoneName: String? = "",
    val zoneLat: String? = "",
    val zoneLong: String? = "",
    val zoneRadius: String? = "",
    val zonePhoneNumber: String? = "",
    val zoneType: String? = "",
    val documentId: String? = ""
) : Parcelable
