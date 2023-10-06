package com.hailm.mapinvitedemo.ui.notification

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class NotificationUiModel(
    val id: Long = 0,
    val phoneNumber: String = "",
    var title: String = "",
    val body: String = "",
    val zoneName: String = "",
    val createTime: String = "",
) : Parcelable
