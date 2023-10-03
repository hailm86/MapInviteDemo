package com.hailm.mapinvitedemo.ui.invite_list

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserInviteUiModel(
    val userOne: String? = "",
    val userTwo: String? = "",
    var status: String? = "-1",
    var documentId: String? = ""
) : Parcelable
