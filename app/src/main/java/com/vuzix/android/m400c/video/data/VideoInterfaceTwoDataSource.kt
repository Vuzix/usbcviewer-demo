package com.vuzix.android.m400c.video.data

import android.hardware.usb.UsbManager
import com.vuzix.android.m400c.common.data.InboundDataSource
import com.vuzix.android.m400c.common.domain.entity.VuzixVideoDevice
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject

class VideoInterfaceTwoDataSource @Inject constructor(
    externalScope: CoroutineScope,
    usbManager: UsbManager,
    videoDevice: VuzixVideoDevice,
    inboundVideoInterfaceTwo: InboundVideoInterfaceTwo
) : InboundDataSource(
    externalScope,
    usbManager,
    videoDevice.usbDevice!!,
    inboundVideoInterfaceTwo
)