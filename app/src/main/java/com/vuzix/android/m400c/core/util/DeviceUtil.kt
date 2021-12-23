package com.vuzix.android.m400c.core.util

import android.hardware.usb.UsbManager
import com.vuzix.android.m400c.common.domain.entity.VuzixAudioDevice
import com.vuzix.android.m400c.common.domain.entity.VuzixHidDevice
import com.vuzix.android.m400c.common.domain.entity.VuzixVideoDevice

object DeviceUtil {

    fun getHidDevice(usbManager: UsbManager): VuzixHidDevice {
        val devices = usbManager.deviceList
        val device = devices.values.first { device -> device.productId == M400cConstants.HID_PID && device.vendorId == M400cConstants.HID_VID }
        return VuzixHidDevice(device)
    }

    fun getVideoDevice(usbManager: UsbManager): VuzixVideoDevice {
        val devices = usbManager.deviceList
        val device = devices.values.first { device -> device.productId == M400cConstants.VIDEO_PID && device.vendorId == M400cConstants.VIDEO_VID }
        return VuzixVideoDevice(device)
    }

    fun getAudioDevice(usbManager: UsbManager): VuzixAudioDevice {
        val devices = usbManager.deviceList
        val device = devices.values.first { device -> device.productId == M400cConstants.AUDIO_PID && device.vendorId == M400cConstants.AUDIO_VID }
        return VuzixAudioDevice(device)
    }
}