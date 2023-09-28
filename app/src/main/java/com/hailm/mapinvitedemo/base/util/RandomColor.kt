package com.hailm.mapinvitedemo.base.util

import com.hailm.mapinvitedemo.R
import kotlin.random.Random

object RandomColor {
    fun randomColor(): Int {
        val list = listOf(
            R.color.random_1,
            R.color.random_2,
            R.color.random_3,
            R.color.random_4,
            R.color.random_5,
            R.color.random_6,
        )
        return list.getOrNull(Random.nextInt() % list.size) ?: R.color.random_1
    }
}
