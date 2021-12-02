package com.vuzix.android.m400c.common.domain.entity

import android.hardware.usb.UsbEndpoint
import android.hardware.usb.UsbInterface

abstract class VuzixBidirectionalInterface(
    open val intf: UsbInterface,
    open val inboundEndpoint: UsbEndpoint,
    open val outboundEndpoint: UsbEndpoint
) : VuzixInterface()
