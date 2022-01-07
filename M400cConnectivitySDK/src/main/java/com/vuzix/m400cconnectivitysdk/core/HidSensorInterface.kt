package com.vuzix.m400cconnectivitysdk.core

import android.hardware.usb.UsbEndpoint
import android.hardware.usb.UsbInterface

data class HidSensorInterface(
    override val intf: UsbInterface,
    override val inboundEndpoint: UsbEndpoint
): VuzixInboundInterface(intf, inboundEndpoint)
