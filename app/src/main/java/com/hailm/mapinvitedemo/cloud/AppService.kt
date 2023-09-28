package com.hailm.mapinvitedemo.cloud

import com.hailm.mapinvitedemo.cloud.response.LoginResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface AppService {
    @POST("api/auth/sign-in")
    @FormUrlEncoded
    suspend fun loginEmail(
        @Field("email") email: String,
        @Field("password") password: String,
    ): LoginResponse
}
