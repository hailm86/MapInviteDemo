package com.hailm.mapinvitedemo.base.interfaces

import androidx.lifecycle.ViewModel

interface ConsumableEvent {
    val viewModelList: List<ViewModel>
        get() = mutableListOf()
}
