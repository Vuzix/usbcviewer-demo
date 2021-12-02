package com.vuzix.android.m400c.hid.domain

data class AccelData(
    val timestamp: Long,
    val accelX: Int,
    val accelY: Int,
    val accelZ: Int
)
