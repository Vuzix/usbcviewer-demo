package com.vuzix.m400cconnectivitysdk.core

import android.hardware.usb.UsbDeviceConnection
import com.vuzix.m400cconnectivitysdk.core.Either.Success
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import timber.log.Timber
import com.vuzix.m400cconnectivitysdk.core.Failure as Fail

abstract class InboundDataSource(
    private val inboundInterface: VuzixInboundInterface
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