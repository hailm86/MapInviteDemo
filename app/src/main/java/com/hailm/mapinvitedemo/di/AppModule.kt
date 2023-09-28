package com.hailm.mapinvitedemo.di

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import androidx.security.crypto.MasterKeys
import com.google.firebase.firestore.FirebaseFirestore
import com.hailm.mapinvitedemo.cloud.AppService
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {
    companion object {
        const val SHARE_PREF = "share_pref"
    }

    @Singleton
    @Provides
    fun provideService(retrofit: Retrofit): AppService = retrofit.create(AppService::class.java)

    @Singleton
    @Provides
    fun provideEncryptedSharePreference(@ApplicationContext context: Context): SharedPreferences {
        return EncryptedSharedPreferences.create(
            context,
            SHARE_PREF,
            MasterKey.Builder(context)
                .setKeyGenParameterSpec(MasterKeys.AES256_GCM_SPEC)
                .build(),
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    @Singleton
    @Provides
    fun provideMoshi(): Moshi {
        return Moshi.Builder().build()
    }

    @Singleton
    @Provides
    fun provideFireStore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }
}
