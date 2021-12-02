package com.vuzix.android.m400c.common.data

import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbDeviceConnection
import android.hardware.usb.UsbEndpoint
import android.hardware.usb.UsbManager
import com.vuzix.android.m400c.common.domain.entity.VuzixOutboundInterface
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

abstract class OutboundDataSource(
    val externalScope: CoroutineScope,
    usbManager: UsbManager,
    usbDevice: UsbDevice,
    outboundInterface: VuzixOutboundInterface
) {

    private val connection: UsbDeviceConnection = usbManager.openDevice(usbDevice)
    private val endpoint: UsbEndpoint = outboundInterface.outboundEndpoint

    fun write(byteArray: ByteArray) = write(byteArray, byteArray.size,0)

    fun write(byteArray: ByteArray, size: Int) = write(byteArray, size,0)

    fun write(byteArray: ByteArray, size: Int, offset: Int) {
        val newByteArray = if (offset != 0) {
            byteArray.copyOfRange(offset, size)
        } else {
            byteArray
        }
        externalScope.launch {
            connection.bulkTransfer(endpoint, newByteArray, size, 1000)
        }
    }
}