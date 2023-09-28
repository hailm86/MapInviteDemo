package com.hailm.mapinvitedemo.base.model.response

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class ErrorResponse(
    @Json(name = "error")
    val error: String = "",
    @Json(name = "key")
    val key: String = "",
    @Json(name = "message")
    val message: String = "",
    @Json(name = "statusCode")
    val statusCode: Int = 0
) : Parcelable