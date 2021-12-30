package com.vuzix.m400cconnectivitysdk.core

data class MagData(
    val timestamp: Long,
    val magX: Float,
    val magY: Float,
    val magZ: Float
)
