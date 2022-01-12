package com.vuzix.sdk.usbcviewer.util

import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import com.vuzix.sdk.usbcviewer.M400cConstants

object DeviceUtil {

    fun getHidDevice(usbManager: UsbManager): UsbDevice? {
        val devices = usbManager.deviceList
        return devices.values.firstOrNull { device -> device.productId == M400cConstants.HID_PID && device.vendorId == M400cConstants.HID_VID }
    }

    fun getVideoDevice(usbManager: UsbManager): UsbDevice? {
        val devices = usbManager.deviceList
        return devices.values.firstOrNull { device -> device.productId == M400cConstants.VIDEO_PID && device.vendorId == M400cConstants.VIDEO_VID }
    }

    fun getAudioDevice(usbManager: UsbManager): UsbDevice? {
        val devices = usbManager.deviceList
        return devices.values.firstOrNull { device -> device.productId == M400cConstants.AUDIO_PID && device.vendorId == M400cConstants.AUDIO_VID }
    }
}