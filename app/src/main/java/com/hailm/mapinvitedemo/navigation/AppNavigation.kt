package com.hailm.mapinvitedemo.navigation

import android.os.Bundle
import com.hailm.mapinvitedemo.base.navigation.BaseNavigator

interface AppNavigation : BaseNavigator {
    fun openSplashToHomeScreen(bundle: Bundle? = null)

    fun openSplashToLoginScreen(bundle: Bundle? = null)
}