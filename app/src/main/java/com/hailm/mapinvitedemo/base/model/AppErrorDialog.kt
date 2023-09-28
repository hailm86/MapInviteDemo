package com.hailm.mapinvitedemo.base.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AppErrorDialog(
    val title: String = "",
    val message: String = ""
) : Parcelable
