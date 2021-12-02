package com.vuzix.android.m400c.hid.data.model

import android.hardware.usb.UsbEndpoint
import android.hardware.usb.UsbInterface
import com.vuzix.android.m400c.common.domain.entity.VuzixBidirectionalInterface
import com.vuzix.android.m400c.common.domain.entity.VuzixInboundInterface

//data class ViewerControlInterface(
//    override val intf: UsbInterface,
//    override val inboundEndpoint: UsbEndpoint,
//    override val outboundEndpoint: UsbEndpoint
//): VuzixBidirectionalInterface(intf, inboundEndpoint, outboundEndpoint)

data class ViewerControlInterface(
    override val intf: UsbInterface,
    override val inboundEndpoint: UsbEndpoint,
): VuzixInboundInterface(intf, inboundEndpoint)