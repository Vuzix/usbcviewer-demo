package com.vuzix.android.m400c.hid.domain

data class MagData(
    val timestamp: Long,
    val magX: Float,
    val magY: Float,
    val magZ: Float
)
