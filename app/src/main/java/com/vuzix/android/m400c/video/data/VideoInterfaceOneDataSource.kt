package com.vuzix.android.m400c.video.data

import android.hardware.usb.UsbManager
import com.vuzix.android.m400c.common.data.InboundDataSource
import com.vuzix.android.m400c.common.domain.entity.VuzixVideoDevice
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject

class VideoInterfaceOneDataSource @Inject constructor(
    externalScope: CoroutineScope,
    usbManager: UsbManager,
    videoDevice: VuzixVideoDevice,
    inboundVideoInterfaceOne: InboundVideoInterfaceOne
) : InboundDataSource(
    externalScope,
    usbManager,
    videoDevice.usbDevice!!,
    inboundVideoInterfaceOne
)