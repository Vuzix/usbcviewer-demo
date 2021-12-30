package com.vuzix.android.m400c.common.data

import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbDeviceConnection
import android.hardware.usb.UsbManager
import com.vuzix.android.m400c.common.domain.entity.VuzixInboundInterface
import com.vuzix.android.m400c.core.util.Either
import com.vuzix.android.m400c.core.util.Either.Success
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import timber.log.Timber
import com.vuzix.android.m400c.core.util.Failure as Fail

abstract class InboundDataSource(
    val inboundInterface: VuzixInboundInterface
) {
    private val _dataFlow = MutableSharedFlow<Either<InboundDataSourceFailure, ByteArray>>(replay = 0)
    val dataFlow: SharedFlow<Either<InboundDataSourceFailure, ByteArray>> = _dataFlow
    var getData = false

    suspend fun initStream(connection: UsbDeviceConnection) {
        Timber.d("Init Stream")
        while (true) {
            if (getData) {
                val bytes = ByteArray(inboundInterface.inboundEndpoint.maxPacketSize)
                val read = connection.bulkTransfer(inboundInterface.inboundEndpoint, bytes, inboundInterface.inboundEndpoint.maxPacketSize, 1000)
//                Timber.d("$read")
                if (read <= inboundInterface.inboundEndpoint.maxPacketSize && read != -1) {
                    _dataFlow.emit(Success(bytes.take(read).toByteArray()))
                }
            }
            delay(inboundInterface.inboundEndpoint.interval.toLong())
        }
    }

    fun getSubCount(): Int = _dataFlow.subscriptionCount.value

    open fun startStream() {
        Timber.d("Start Stream")
        getData = true
    }

    fun stopStream() {
        Timber.d("Stop Stream")
        getData = false
    }

    data class InboundDataSourceFailure(val error: String) : Fail.DataFailure()
}