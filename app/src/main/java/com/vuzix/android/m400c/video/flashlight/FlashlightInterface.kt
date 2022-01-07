package com.vuzix.android.m400c.video.flashlight

import android.hardware.usb.UsbEndpoint
import android.hardware.usb.UsbInterface
import com.vuzix.m400cconnectivitysdk.core.VuzixInboundInterface

data class FlashlightInterface(
    override val intf: UsbInterface,
    override val inboundEndpoint: UsbEndpoint
): VuzixInboundInterface(intf, inboundEndpoint)
