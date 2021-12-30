package com.vuzix.m400cconnectivitysdk.core

class UsbHidDevice {
    companion object {
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

        fun getGyroValues() {

        }
    }
}