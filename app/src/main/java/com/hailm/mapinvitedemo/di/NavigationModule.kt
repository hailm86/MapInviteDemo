package com.hailm.mapinvitedemo.di

import com.hailm.mapinvitedemo.base.navigation.BaseNavigator
import com.hailm.mapinvitedemo.navigation.AppNavigation
import com.hailm.mapinvitedemo.navigation.AppNavigatorImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent

@Module
@InstallIn(ActivityComponent::class)
abstract class NavigationModule {

    @Binds
    abstract fun provideBaseNavigation(navigation: AppNavigatorImpl): BaseNavigator

    @Binds
    abstract fun provideAppNavigation(navigation: AppNavigatorImpl): AppNavigation
}
