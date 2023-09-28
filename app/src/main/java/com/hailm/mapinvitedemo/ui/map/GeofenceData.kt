package com.hailm.mapinvitedemo.ui.map

data class GeofenceData(
    val id: String,
    val latitude: Double,
    val longitude: Double,
    val radius: Float,
    val transitionTypes: Int
)
