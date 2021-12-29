package com.vuzix.android.m400c.core.util

import com.vuzix.android.m400c.hid.domain.AccelData
import com.vuzix.android.m400c.hid.domain.GyroData
import com.vuzix.android.m400c.hid.domain.MagData
import timber.log.Timber
import java.util.concurrent.TimeUnit
import kotlin.math.sqrt

object SensorUtil {

    private const val EPSILON = 0.000000001f

    // TODO: This should create the rad/s value to be displayed. Only does raw values right now.
    fun createGyroObject(bytes: ByteArray): GyroData {
        val timestamp = TimeUnit.MILLISECONDS.toNanos(System.currentTimeMillis())
        val gyroX = (bytes[4].toInt() shl 8) or bytes[3].toInt()
        val gyroY = (bytes[6].toInt() shl 8) or bytes[5].toInt()
        val gyroZ = (bytes[8].toInt() shl 8) or bytes[7].toInt()

        var axisX: Float = gyroX.toFloat()
        var axisY: Float = gyroY.toFloat()
        var axisZ: Float = gyroZ.toFloat()

        val omegaMagnitude: Float = sqrt(axisX * axisX + axisY * axisY + axisZ * axisZ)

        if (omegaMagnitude > EPSILON) {
            axisX /= omegaMagnitude
            axisY /= omegaMagnitude
            axisZ /= omegaMagnitude
        }
        return GyroData(timestamp, gyroX.toFloat(), gyroY.toFloat(), gyroZ.toFloat())
    }

    // TODO: This should create the accel value. Only does raw values right now.
    fun createAccelData(bytes: ByteArray): AccelData {
        return AccelData(
            TimeUnit.MILLISECONDS.toNanos(System.currentTimeMillis()),
            ((bytes[4].toInt() shl 8) or bytes[3].toInt()),
            ((bytes[6].toInt() shl 8) or bytes[5].toInt()),
            ((bytes[8].toInt() shl 8) or bytes[7].toInt())
        )
    }

    // TODO: This should create a mag value. Only does raw values right now.
    fun createMagData(bytes: ByteArray): MagData {
        return MagData(
            TimeUnit.MILLISECONDS.toNanos(System.currentTimeMillis()),
            ((bytes[4].toInt() shl 8) or bytes[3].toInt()).toFloat(),
            ((bytes[6].toInt() shl 8) or bytes[5].toInt()).toFloat(),
            ((bytes[8].toInt() shl 8) or bytes[7].toInt()).toFloat()
        )
    }

    fun getSensorControlPacket(sensorId: Int, reportInterval: Long): ByteArray {
        return byteArrayOf(
            sensorId.toByte(), // 0
            2, // 1
            2, // 2
            2, // 3
            2, // 4
            (reportInterval and 0xFF).toByte(), // 5
            ((reportInterval shr 8) and 0xFF).toByte(), // 6
            ((reportInterval shr 16) and 0xFF).toByte(), // 7
            ((reportInterval shr 24) and 0xFF).toByte(), // 8
            0, // 9
            0, // 10
            (0xFF).toByte(), // 11
            0x1F, // 12
            0, // 13
            0 // 14
        )
    }
}