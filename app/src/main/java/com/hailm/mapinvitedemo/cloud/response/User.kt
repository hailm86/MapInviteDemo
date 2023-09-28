package com.hailm.mapinvitedemo.cloud.response

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class User(
    @Json(name = "email")
    val email: String = "",
    @Json(name = "firstName")
    val firstName: String = "",
    @Json(name = "_id")
    val id: String = "",
    @Json(name = "lastName")
    val lastName: String = "",
    @Json(name = "gender")
    val gender: String = "",
    @Json(name = "position")
    val position: String = "",
    @Json(name = "dateOfBirth")
    val dateOfBirth: String = "",
    @Json(name = "provider")
    val provider: String = "",
    @Json(name = "createdAt")
    val createdAt: String = "",
    @Json(name = "updatedAt")
    val updatedAt: String = ""
) : Parcelable
