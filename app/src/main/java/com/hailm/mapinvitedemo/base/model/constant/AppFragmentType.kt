package com.hailm.mapinvitedemo.base.model.constant

import androidx.annotation.ColorRes
import androidx.annotation.IdRes
import com.hailm.mapinvitedemo.R

enum class AppFragmentType(
    @IdRes val destinationId: Int,
    val showBottomNavigation: Boolean = false,
    @ColorRes val statusBar: Int = R.color.transparent,
    @ColorRes val navigationBar: Int = R.color.transparent,
    val isDialog: Boolean = false
) {
    SPLASH(R.id.splashFragment, showBottomNavigation = false),
    HOME(R.id.homeFragment, showBottomNavigation = false),
    COMMUNITY(R.id.communityFragment, showBottomNavigation = false),
    PROFILE(R.id.profileFragment, showBottomNavigation = false),
    UNKNOWN(-1);

    companion object {
        private val destinationIdMap = values().associateBy(AppFragmentType::destinationId)
        fun fromDestinationId(destinationId: Int) = destinationIdMap[destinationId] ?: UNKNOWN
    }
}
