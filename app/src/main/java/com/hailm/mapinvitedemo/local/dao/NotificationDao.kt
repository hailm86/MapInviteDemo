package com.hailm.mapinvitedemo.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.hailm.mapinvitedemo.local.entity.NotificationEntity

@Dao
abstract class NotificationDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun insert(notification: NotificationEntity)

    @Transaction
    @Query("SELECT * FROM NotificationEntity WHERE phoneNumber = :phoneNumber")
    abstract fun findNotification(phoneNumber: String): List<NotificationEntity>
}
