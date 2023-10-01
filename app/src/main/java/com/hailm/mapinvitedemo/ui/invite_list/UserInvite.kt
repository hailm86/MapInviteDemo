package com.hailm.mapinvitedemo.ui.invite_list

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserInvite(
    val userOne: String? = "",
    val userTwo: String? = "",
    var status: String? = "-1"
) : Parcelable
