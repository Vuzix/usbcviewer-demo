package com.vuzix.android.m400c.hid.data.source

import android.hardware.usb.UsbManager
import com.vuzix.android.m400c.common.data.InboundDataSource
import com.vuzix.android.m400c.common.domain.entity.VuzixHidDevice
import com.vuzix.android.m400c.hid.data.model.ViewerControlInterface
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject

class ViewerControlDataSource @Inject constructor(
    externalScope: CoroutineScope,
    usbManager: UsbManager,
    hidDevice: VuzixHidDevice,
    viewerControlInterface: ViewerControlInterface
) : InboundDataSource(
    externalScope,
    usbManager,
    hidDevice.usbDevice!!,
    viewerControlInterface
)