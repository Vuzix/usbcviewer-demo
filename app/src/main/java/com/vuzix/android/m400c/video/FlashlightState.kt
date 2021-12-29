package com.vuzix.android.m400c.video

sealed class FlashlightState {
    object On : FlashlightState()
    object Off : FlashlightState() // Default
}
