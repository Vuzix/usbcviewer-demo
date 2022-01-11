package com.vuzix.m400cconnectivitysdk.sensor

import android.hardware.usb.UsbEndpoint
import android.hardware.usb.UsbInterface
import com.vuzix.m400cconnectivitysdk.core.VuzixInboundInterface

data class HidSensorInterface(
    override val intf: UsbInterface,
    override val inboundEndpoint: UsbEndpoint
): VuzixInboundInterface(intf, inboundEndpoint)
