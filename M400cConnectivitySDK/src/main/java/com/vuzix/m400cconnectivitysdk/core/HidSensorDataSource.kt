package com.vuzix.m400cconnectivitysdk.core

import android.hardware.usb.UsbDeviceConnection
import android.hardware.usb.UsbManager
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*

class HidSensorDataSource constructor(
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

    fun initConnection(): Flow<Boolean> = flow {
        connection = usbManager.openDevice(hidDevice.usbDevice)
        if (!connection.claimInterface(hidSensorInterface.intf, true)) {
            emit(false)
        }
        connection.setInterface(hidSensorInterface.intf)
        emit(true)
    }

    fun initSensor(sensor: Int): Flow<Boolean> = flow {
        val bytes = SensorUtil.getSensorControlPacket(sensor, 4) // 60Hz sample rate for all sensors, since they each get their own packet
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
            Log.i("HidSensoryDataSource", "Received 0x${Arrays.toString(incomingBytes)}")
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