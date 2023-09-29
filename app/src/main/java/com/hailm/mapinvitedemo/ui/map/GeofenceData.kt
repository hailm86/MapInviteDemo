package com.hailm.mapinvitedemo.ui.map

data class GeofenceData(
    val id: String,
    var latitude: Double,
    var longitude: Double,
    val radius: Float,
    val transitionTypes: Int
)
