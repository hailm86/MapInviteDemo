package com.hailm.mapinvitedemo.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.hailm.mapinvitedemo.local.dao.NotificationDao
import com.hailm.mapinvitedemo.local.entity.NotificationEntity

@Database(
    entities = [
        NotificationEntity::class,
    ],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    companion object {
        private const val DB_NAME = "database_app.db"
        // Array of all migrations
        // private val ALL_MIGRATIONS = arrayOf()

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java, DB_NAME
            )
                .allowMainThreadQueries()
                .build()

    }

    abstract fun notificationDao(): NotificationDao


    suspend fun insertNotificationHistory(notificationEntity: NotificationEntity) {
        notificationDao().insert(notificationEntity)
    }

    fun loadListNoti(phone: String) = notificationDao().findNotification(phone)
}