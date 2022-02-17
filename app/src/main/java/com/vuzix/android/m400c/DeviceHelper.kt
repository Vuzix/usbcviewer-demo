package com.vuzix.android.m400c

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.hardware.usb.UsbManager
import com.vuzix.sdk.usbcviewer.M400cConstants
import timber.log.Timber

object DeviceHelper {

    /**
     * Function to set up a [BroadcastReceiver] to be used in order to
     * facilitate requesting permission to use a Vuzix USB-C device.
     *
     * @param usbManager Used as part of the [onReceive] to check permissions.
     */
    fun setupReceiver(usbManager: UsbManager): BroadcastReceiver {
        val usbReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                if (M400cConstants.ACTION_USB_PERMISSION == intent.action) {
                    Timber.d("RECEIVED")
                    checkPermissions(usbManager, context)
                }
            }
        }
        return usbReceiver
    }

    /**
     * Function used to check usb device permissions. Runs a for-loop to cycle
     * through each device (a Vuzix USB-C device has is technically 3 devices)
     * and if [hasPermission] is false, it makes the request. Coupled with the
     * receiver, it ensures that all permission checks occur in order, thereby
     * preventing a situation where a necessary permission hasn't been checked
     * for or granted, which can lead to a crash.
     *
     * The preference is still for requesting permission at the point of use,
     * but this allows for the alternative of front-loading the process.
     *
     * @param usbManager Needed to both check for and request permission.
     * @param context If permission needs to be requested, this allows for the
     * creation of a [PendingIntent].
     */
    fun checkPermissions(usbManager: UsbManager, context: Context) {
        Timber.d("CHECKING")
        val devices = usbManager.deviceList.values.toList()
        for (i in devices.indices) {
            if (!usbManager.hasPermission(devices[i])) {
                val usbPermissionIntent = PendingIntent.getBroadcast(
                    context,
                    0,
                    Intent(M400cConstants.ACTION_USB_PERMISSION),
                    PendingIntent.FLAG_IMMUTABLE
                )
                usbManager.requestPermission(devices[i], usbPermissionIntent)
                break
            }
        }
    }
}