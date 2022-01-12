package com.vuzix.sdk.usbcviewer.flashlight

import android.content.Context
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbDeviceConnection
import android.hardware.usb.UsbInterface
import android.hardware.usb.UsbManager
import com.vuzix.sdk.usbcviewer.M400cConstants
import com.vuzix.sdk.usbcviewer.util.DeviceUtil
import com.vuzix.sdk.usbcviewer.util.LogUtil
import java.util.concurrent.TimeUnit

/**
 * This class allows you to access the Flashlight/Torch in order to turn
 * it on or off.
 *
 * @param context App context, necessary to initialize an instance of
 * [UsbManager].
 */
class Flashlight(context: Context) {
    private val usbManager: UsbManager = context.getSystemService(Context.USB_SERVICE) as UsbManager
    private var usbDevice: UsbDevice? = null
    private lateinit var connection: UsbDeviceConnection
    private lateinit var flashlightInterface: UsbInterface

    /** Exposed property that can be used to determine if there is an active [UsbDeviceConnection] */
    var connected = false
    private set

    /**
     * Function used to create the [UsbDeviceConnection]
     * needed in order to send the commands for turning the Flashlight/Torch
     * on or off.
     *
     * @throws Exception if, when attempting to get the video [UsbDevice]
     * the device returns as null, or if when attempting to [claimInterface()] the
     * result is false.
     */
    @Throws(Exception::class)
    fun connect() {
        LogUtil.debug("connect")
        usbDevice = DeviceUtil.getHidDevice(usbManager)
        usbDevice?.let {
            flashlightInterface = it.getInterface(M400cConstants.VIDEO_HID)
            connection = usbManager.openDevice(it)
            if (!connection.claimInterface(flashlightInterface, true)) {
                throw Exception("Failed to claim Flashlight Interface")
            }
            connected = connection.setInterface(flashlightInterface)
        } ?: throw Exception("Video Device is null")
    }

    /**
     * Function used to close down the [UsbDeviceConnection].
     */
    fun disconnect() {
        LogUtil.debug("disconnect")
        try {
            connection.releaseInterface(flashlightInterface)
            connection.close()
        } catch (e: Exception) {
            // Eat it
        }
        connected = false
        usbDevice = null
    }

    /**
     * Function used to let you know if the video [UsbDevice] is null or not.
     *
     * @return True if not null.
     */
    fun isDeviceAvailable(): Boolean {
        return usbDevice?.let {
            true
        } ?: run {
            usbDevice = DeviceUtil.getHidDevice(usbManager)
            usbDevice != null
        }
    }

    /**
     * Function used to turn the flashlight/torch on.
     */
    @Throws(Exception::class)
    fun turnFlashlightOn() {
        LogUtil.debug("turnFlashlightOn")
        changeFlashlightState(getFlashlightPacket(true))
    }

    /**
     * Function used to turn the flashlight/torch off.
     */
    @Throws(Exception::class)
    fun turnFlashlightOff() {
        LogUtil.debug("turnFlashlightOn")
        changeFlashlightState(getFlashlightPacket(false))
    }

    private fun changeFlashlightState(byteArray: ByteArray) {
        if (!connected) {
            throw Exception("Device is not connected")
        }
        connection.controlTransfer(
            0x21,
            0x09,
            0x0200,
            flashlightInterface.id,
            byteArray,
            byteArray.size,
            TimeUnit.SECONDS.toMillis(1).toInt()
        )
    }

    // This function simply generates the On/Off ByteArray payload.
    private fun getFlashlightPacket(turnOn: Boolean): ByteArray {
        return if (turnOn) {
            byteArrayOf(2, M400cConstants.FLASHLIGHT_ON.toByte(), 0x01)
        } else {
            byteArrayOf(2, M400cConstants.FLASHLIGHT_OFF.toByte(), 0x01)
        }
    }
}