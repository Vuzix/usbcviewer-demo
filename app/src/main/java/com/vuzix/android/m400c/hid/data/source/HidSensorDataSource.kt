package com.vuzix.android.m400c.hid.data.source

import android.hardware.usb.UsbDeviceConnection
import android.hardware.usb.UsbEndpoint
import android.hardware.usb.UsbInterface
import android.hardware.usb.UsbManager
import com.vuzix.android.m400c.common.data.InboundDataSource
import com.vuzix.android.m400c.common.data.InboundDataSourceFailure
import com.vuzix.android.m400c.common.domain.entity.VuzixHidDevice
import com.vuzix.android.m400c.core.util.Either
import com.vuzix.android.m400c.core.util.Either.Failure
import com.vuzix.android.m400c.core.util.Either.Success
import com.vuzix.android.m400c.core.util.M400cConstants
import com.vuzix.android.m400c.core.util.SensorUtil
import com.vuzix.android.m400c.core.util.UsbHidDevice
import com.vuzix.android.m400c.core.util.strPrint
import com.vuzix.android.m400c.hid.data.model.HidSensorInterface
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class HidSensorDataSource @Inject constructor(
    val externalScope: CoroutineScope,
    val usbManager: UsbManager,
    val hidDevice: VuzixHidDevice,
    private val hidSensorInterface: HidSensorInterface
) : InboundDataSource(
    externalScope,
    usbManager,
    hidDevice.usbDevice!!,
    hidSensorInterface
) {
    lateinit var connection: UsbDeviceConnection

    fun initConnection(): Flow<Boolean> =  flow {
        connection = usbManager.openDevice(hidDevice.usbDevice)
        if (!connection.claimInterface(hidSensorInterface.intf, true)) {
            emit(false)
        }
        connection.setInterface(hidSensorInterface.intf)
        emit(true)
    }

    fun initSensor(sensor: Int): Flow<Boolean> = flow {
        val bytes = SensorUtil.getSensorControlPacket(sensor, 120)
        connection.controlTransfer(
            0x21,
            0x09,
            0x0300 or sensor,
            hidSensorInterface.intf.id,
            bytes,
            bytes.size,
            1000
        )
        val incomingBytes = ByteArray(hidSensorInterface.inboundEndpoint.maxPacketSize)
        val read = connection.controlTransfer(
            0xA1,
            0x01,
            0x0300 or sensor,
            hidSensorInterface.intf.id,
            incomingBytes,
            incomingBytes.size,
            1000
        )
        if (read > 0) {
            Timber.d("Received 0x${incomingBytes.strPrint()}")
        }
        emit(true)
    }

    override fun startStream() {
        if (getSubCount() < 1) {
            externalScope.launch {
                Timber.d("Start Stream")
                initStream(connection) }
        }
        super.startStream()
    }
}