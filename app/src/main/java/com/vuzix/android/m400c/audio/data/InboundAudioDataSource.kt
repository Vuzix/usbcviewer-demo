package com.vuzix.android.m400c.audio.data

import android.hardware.usb.UsbManager
import com.vuzix.android.m400c.common.data.InboundDataSource
import com.vuzix.android.m400c.common.domain.entity.VuzixAudioDevice
import com.vuzix.android.m400c.common.domain.entity.VuzixHidDevice
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject

class InboundAudioDataSource @Inject constructor(
    externalScope: CoroutineScope,
    usbManager: UsbManager,
    audioDevice: VuzixAudioDevice,
    inboundAudioInterface: InboundAudioInterface
) : InboundDataSource(
    externalScope,
    usbManager,
    audioDevice.usbDevice!!,
    inboundAudioInterface
)