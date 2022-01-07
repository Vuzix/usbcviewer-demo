package com.vuzix.m400cconnectivitysdk.core

import android.hardware.usb.UsbEndpoint
import android.hardware.usb.UsbInterface

abstract class VuzixInboundInterface(
    open val intf: UsbInterface,
    open val inboundEndpoint: UsbEndpoint
) : VuzixInterface()
