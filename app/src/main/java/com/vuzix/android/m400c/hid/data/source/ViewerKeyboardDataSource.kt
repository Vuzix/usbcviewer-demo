package com.vuzix.android.m400c.hid.data.source

import android.hardware.usb.UsbDeviceConnection
import android.hardware.usb.UsbManager
import com.vuzix.android.m400c.common.data.InboundDataSource
import com.vuzix.android.m400c.common.domain.entity.VuzixHidDevice
import com.vuzix.android.m400c.hid.data.model.ViewerKeyboardInterface
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject

class ViewerKeyboardDataSource @Inject constructor(
    val externalScope: CoroutineScope,
    val usbManager: UsbManager,
    val hidDevice: VuzixHidDevice,
    val hidViewerKeyboardInterface: ViewerKeyboardInterface
) : InboundDataSource(
    externalScope,
    usbManager,
    hidDevice.usbDevice!!,
    hidViewerKeyboardInterface
) {

    lateinit var connection: UsbDeviceConnection

    fun initConnection(): Flow<Boolean> = flow {
        connection = usbManager.openDevice(hidDevice.usbDevice)
        connection.claimInterface(hidViewerKeyboardInterface.intf, true)
        connection.setInterface(hidViewerKeyboardInterface.intf)
        emit(true)
    }

    override fun startStream() {
        if (getSubCount() < 1) {
            externalScope.launch {
                initStream(connection)
            }
        }
        super.startStream()
    }
}