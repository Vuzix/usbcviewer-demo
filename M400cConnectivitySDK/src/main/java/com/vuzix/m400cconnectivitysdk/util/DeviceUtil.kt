package com.vuzix.m400cconnectivitysdk.util

import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import com.vuzix.m400cconnectivitysdk.M400cConstants
import com.vuzix.m400cconnectivitysdk.audio.VuzixAudioDevice
import com.vuzix.m400cconnectivitysdk.sensor.VuzixHidDevice
import com.vuzix.m400cconnectivitysdk.video.VuzixVideoDevice

object DeviceUtil {

    fun getHidDevice(usbManager: UsbManager): VuzixHidDevice {
        val devices = usbManager.deviceList
        val device: UsbDevice? = devices.values.first { device -> device.productId == M400cConstants.HID_PID && device.vendorId == M400cConstants.HID_VID }
        return VuzixHidDevice(device)
    }

    fun getVideoDevice(usbManager: UsbManager): VuzixVideoDevice {
        val devices = usbManager.deviceList
        val device: UsbDevice? = devices.values.firstOrNull { device -> device.productId == M400cConstants.VIDEO_PID && device.vendorId == M400cConstants.VIDEO_VID }
        return VuzixVideoDevice(device)
    }

    fun getAudioDevice(usbManager: UsbManager): VuzixAudioDevice {
        val devices = usbManager.deviceList
        val device: UsbDevice? = devices.values.first { device -> device.productId == M400cConstants.AUDIO_PID && device.vendorId == M400cConstants.AUDIO_VID }
        return VuzixAudioDevice(device)
    }
}