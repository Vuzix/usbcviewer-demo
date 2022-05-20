# Vuzix USB-C Viewer Android Support
## Overview
This Android demo project shows the features of the Vuzix [sdk-usbcviewer](https://github.com/Vuzix/sdk-usbcviewer) Android development SDK.

Please note that Vuzix smart glasses fall into two categories:
1. Stand-alone smart glasses, such as the M400, which are *not* supported by this demo application.
2. USB-C Viewers, such as the M400C, for which this demo application is intended.

The USB-C Viewer must be connected via a USB-C cable to an Android phone supporting USB-C DisplayPort Alt Mode. Please note, many popular smart phones 
do *not* support this capability. Please check with your phone manufacturer to determine if your device is compatible.

## Supported Features
This demonstration shows the feature set of the USB-C viewer when connected to a supported Android phone.

1. Display - This demo shows the phone screen mirrored to the USB-C viewer.
2. Audio - This demo shows the phone using the microphone and speaker of the USB-C viewer.
3. Button input - This demo shows the buttons on the USB-C viewer being processed by the phone.
4. Touchpad input - This demo shows the touchpad on the USB-C viewer moving a mouse pointer on the phone.
5. Orientation Sensor - This demo shows the positional sensors of the USB-C viewer being processed by the phone.
6. Flashlight - This demo shows the ability to control the light on the USB-C viewer from the phone.
7. Camera - This demo shows the ability of the phone to receive the live camera stream from the USB-C viewer.

## Building and Installation
The demo application is built using Android Studio. The resulting .apk file may be directly installed to your phone.

For developers modifying the source code, it is recommended to connect your development computer to the phone over TCP/IP and connect
the USB-C viewer to the phone. Details on this connection are described in the
[Android Debug Bridge](https://developer.android.com/studio/command-line/adb#wireless) documentation.


