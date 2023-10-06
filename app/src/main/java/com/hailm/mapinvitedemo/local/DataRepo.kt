package com.hailm.mapinvitedemo.local

import com.hailm.mapinvitedemo.local.entity.NotificationEntity

interface DataRepo {
    suspend fun insertNotification(notificationEntity: NotificationEntity)

    suspend fun loadListNoti(phoneNumber: String): List<NotificationEntity>
}