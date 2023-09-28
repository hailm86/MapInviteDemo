package com.hailm.mapinvitedemo.cloud.response

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class LoginResponse(
    @Json(name = "data")
    val loginResponseData: LoginResponseData
) : Parcelable


@Parcelize
@JsonClass(generateAdapter = true)
data class LoginResponseData(
    @Json(name = "token")
    val token: String = "",
    @Json(name = "user")
    val user: User = User()
) : Parcelable
