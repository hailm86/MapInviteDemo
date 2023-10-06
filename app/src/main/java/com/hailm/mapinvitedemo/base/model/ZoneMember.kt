package com.hailm.mapinvitedemo.base.model

import com.google.firebase.Timestamp

data class ZoneMember(
    val documentIdZoneAlert: String,
    val isInsideGeofence: String,
    val memberName: String,
    val updateTime: Timestamp,
    val zoneMember: String
)
