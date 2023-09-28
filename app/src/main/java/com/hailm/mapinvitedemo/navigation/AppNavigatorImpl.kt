package com.hailm.mapinvitedemo.navigation

import android.os.Bundle
import com.hailm.mapinvitedemo.R
import com.hailm.mapinvitedemo.base.navigation.BaseNavigatorImpl
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

@ActivityScoped
class AppNavigatorImpl @Inject constructor() : BaseNavigatorImpl(), AppNavigation {
    override fun openSplashToHomeScreen(bundle: Bundle?) {
        openScreen(R.id.homeFragment, bundle)
    }

    override fun openSplashToLoginScreen(bundle: Bundle?) {
        openScreen(R.id.loginFragment, bundle)
    }
}
