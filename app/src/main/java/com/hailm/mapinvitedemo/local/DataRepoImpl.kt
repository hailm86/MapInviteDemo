package com.hailm.mapinvitedemo.local

import com.hailm.mapinvitedemo.local.entity.NotificationEntity
import javax.inject.Inject

class DataRepoImpl @Inject constructor(private val database: AppDatabase) : DataRepo {
    override suspend fun insertNotification(notificationEntity: NotificationEntity) {
        database.insertNotificationHistory(notificationEntity)
    }

    override suspend fun loadListNoti(phoneNumber: String): List<NotificationEntity> {
        return database.loadListNoti(phoneNumber)
    }
}
