package com.hailm.mapinvitedemo.base.extension

import com.google.android.gms.maps.model.LatLng
import kotlin.math.*

object LocationUtils {
    // Hàm tính khoảng cách giữa hai tọa độ LatLong theo đơn vị mét
    fun distanceBetween(latLng1: LatLng, latLng2: LatLng): Double {
        val earthRadius = 6371 // Bán kính Trái Đất (đơn vị: km)

        val lat1 = Math.toRadians(latLng1.latitude)
        val lon1 = Math.toRadians(latLng1.longitude)
        val lat2 = Math.toRadians(latLng2.latitude)
        val lon2 = Math.toRadians(latLng2.longitude)

        val dLat = lat2 - lat1
        val dLon = lon2 - lon1

        val a = sin(dLat / 2).pow(2) + cos(lat1) * cos(lat2) * sin(dLon / 2).pow(2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        val distance = earthRadius * c // Khoảng cách giữa hai điểm (đơn vị: km)

        return distance * 1000 // Chuyển đổi thành mét
    }
}
