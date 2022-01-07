package com.vuzix.android.m400c.video.flashlight

sealed class FlashlightState {
    object On : FlashlightState()
    object Off : FlashlightState() // Default
}
