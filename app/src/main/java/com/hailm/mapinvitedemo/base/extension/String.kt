/*
 * Created by Louis Solo on Mar-25
 * Copyright (c) 2020 . All rights reserved.
 */

package com.hailm.mapinvitedemo.base.extension

import android.text.TextUtils
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import java.util.regex.Pattern

const val PHONE_MAX_LENGTH = 16

fun String.Companion.empty() = ""

fun String.Companion.notAvailable() = "Not Available"

fun String.Companion.notNullOrEmpty(value: String?) = null != value && value.trim().isNotEmpty()

fun String?.notNullOrEmpty(): Boolean {
    return null != this && this.trim().isNotEmpty()
}

fun String.isValidEmail(): Boolean {
    return !TextUtils.isEmpty(this) && android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
}

fun String.isValidPhone(): Boolean {
    val regex = "((08|8|\\+628|628).*).{8,14}\$"
    val pattern = Pattern.compile(regex)
    return !TextUtils.isEmpty(this) && android.util.Patterns.PHONE.matcher(this).matches() &&
            pattern.matcher(this).matches() && (this.length <= PHONE_MAX_LENGTH)
}

fun String.isValidNumber(): Boolean {
    val regex = "^\\d+\$"
    val pattern = Pattern.compile(regex)
    return !TextUtils.isEmpty(this) && android.util.Patterns.PHONE.matcher(this).matches() &&
            pattern.matcher(this).matches()
}

fun String.isValidPassword(): Boolean {
    val pwdRegex = "^(?=.*[0-9])(?=.*[A-Z])(?=\\S+\$).{7,20}\$"
    val pattern = Pattern.compile(pwdRegex)
    return !TextUtils.isEmpty(this) && pattern.matcher(this).matches()
}

fun String.toDateFormat(old: String, new: String): String {
    return try {
        val raw = DateTimeFormatter.ofPattern(old)
        val formatter = DateTimeFormatter.ofPattern(new)
        LocalDate.parse(this, raw).format(formatter)
    } catch (e: Exception) {
        ""
    }
}
