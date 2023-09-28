package com.hailm.mapinvitedemo.ui.splash

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.hailm.mapinvitedemo.MainActivity
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashTheme : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MainScope().launch {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                delay(2000L)
            } else {
                delay(1000L)
            }
            val intent = Intent(this@SplashTheme, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
