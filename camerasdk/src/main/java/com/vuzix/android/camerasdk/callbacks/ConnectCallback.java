package com.vuzix.android.camerasdk.callbacks;

import android.hardware.usb.UsbDevice;

public interface ConnectCallback {
    void onAttached(UsbDevice usbDevice);
    void onGranted(UsbDevice usbDevice, boolean granted);
    void onConnected(UsbDevice usbDevice);
    void onCameraOpened();
    void onDetached(UsbDevice usbDevice);
}
