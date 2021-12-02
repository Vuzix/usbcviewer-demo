package com.vuzix.android.m400c.hid.data.model

import android.hardware.usb.UsbEndpoint
import android.hardware.usb.UsbInterface
import com.vuzix.android.m400c.common.domain.entity.VuzixInboundInterface
import dagger.Provides

data class ViewerKeyboardInterface(
    override val intf: UsbInterface,
    override val inboundEndpoint: UsbEndpoint
) : VuzixInboundInterface(intf, inboundEndpoint)
