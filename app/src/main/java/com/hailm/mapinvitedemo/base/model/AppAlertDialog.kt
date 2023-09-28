package com.hailm.mapinvitedemo.base.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AppAlertDialog(
    val title: String = "",
    val message: String = "",
    val button: Int? = null,
    val buttonNegative: Int? = null,
    val isCancel: Boolean = true
) : Parcelable
