package com.hailm.mapinvitedemo.local

import com.hailm.mapinvitedemo.local.entity.NotificationEntity

class DataRepoImpl(private val database: AppDatabase) : DataRepo {
    override suspend fun insertNotification(notificationEntity: NotificationEntity) {
        database.insertNotificationHistory(notificationEntity)
    }

    override suspend fun loadListNoti(phoneNumber: String) {
        database.loadListNoti(phoneNumber)
    }
}
