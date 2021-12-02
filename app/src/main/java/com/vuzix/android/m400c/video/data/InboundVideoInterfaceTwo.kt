package com.vuzix.android.m400c.video.data

import android.hardware.usb.UsbEndpoint
import android.hardware.usb.UsbInterface
import com.vuzix.android.m400c.common.domain.entity.VuzixInboundInterface

data class InboundVideoInterfaceTwo(
    override val intf: UsbInterface,
    override val inboundEndpoint: UsbEndpoint
) : VuzixInboundInterface(intf, inboundEndpoint)
