/*
 * Created by Louis Solo on Apr-04
 * Copyright (c) 2020 . All rights reserved.
 */

package com.hailm.mapinvitedemo.base.extension

inline fun <reified T> toObject(strJson: String?): T? {
    return if (String.notNullOrEmpty(strJson)) {
        val moshi = com.squareup.moshi.Moshi.Builder().build()
        val adapter = moshi.adapter(T::class.java)
        adapter.fromJson(strJson)
    } else {
        null
    }
}

inline fun <reified T> toJson(obj: T?): String {
    return if (null == obj) {
        String.empty()
    } else {
        val moshi = com.squareup.moshi.Moshi.Builder().build()
        val adapter = moshi.adapter(T::class.java)

        adapter.toJson(obj)
    }
}
