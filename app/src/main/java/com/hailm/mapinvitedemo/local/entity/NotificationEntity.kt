package com.hailm.mapinvitedemo.local.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity
data class NotificationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val phoneNumber: String = "",
    var title: String = "",
    val body: String = "",
    val zoneName: String = "",
    val createTime: String = "",
) : Parcelable
