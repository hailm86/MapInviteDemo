package com.hailm.mapinvitedemo.base.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ZoneAlert(
    val zoneName: String? = "",
    val zoneLat: String? = "",
    val zoneLong: String? = "",
    val zoneRadius: String? = "",
    val zonePhoneNumber: String? = "",
    val zoneType: String? = "",
) : Parcelable

