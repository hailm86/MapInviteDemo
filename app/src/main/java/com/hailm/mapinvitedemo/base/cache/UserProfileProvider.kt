package com.hailm.mapinvitedemo.base.cache

import android.content.SharedPreferences
import com.squareup.moshi.Moshi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserProfileProvider @Inject constructor(
    private val moshi: Moshi,

    private val sharedPreferences: SharedPreferences
) {
    companion object {
        const val KEY_USER_PHONE_NUMBER = "key_user_phone_number"
    }

    val userPhoneNumber
        get() = sharedPreferences.getString(KEY_USER_PHONE_NUMBER, "")

    fun saveUserPhoneNumber(userId: String) {
        sharedPreferences.edit()
            .putString(KEY_USER_PHONE_NUMBER, userId)
            .apply()
    }

    fun clearUserId() {
        sharedPreferences.edit()
            .putStringSet(KEY_USER_PHONE_NUMBER, emptySet())
            .apply()
    }

}
