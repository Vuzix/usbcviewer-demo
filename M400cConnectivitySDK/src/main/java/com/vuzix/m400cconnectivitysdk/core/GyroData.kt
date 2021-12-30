package com.vuzix.android.m400c.hid.domain

data class GyroData(
    val timestamp: Long,
    val gyroX: Float,
    val gyroY: Float,
    val gyroZ: Float
)
