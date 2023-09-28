package com.hailm.mapinvitedemo.base.extension

import android.content.Context
import android.view.LayoutInflater

val Context.inflater: LayoutInflater
    get() = LayoutInflater.from(this)