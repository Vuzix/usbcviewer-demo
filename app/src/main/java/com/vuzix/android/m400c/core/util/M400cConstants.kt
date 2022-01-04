package com.vuzix.android.m400c.core.util

object M400cConstants {
    const val ACTION_USB_PERMISSION = "com.android.vuzix.USB_PERMISSION"
    // HID
    const val HID_PID = 458
    const val HID_VID = 7086
    // VIDEO
    const val VIDEO_PID = 195
    const val VIDEO_VID = 1204
    // AUDIO
    const val AUDIO_PID = 22529
    const val AUDIO_VID = 1156

    // IDs for the HID interfaces
    const val HID_VIEWER_CONTROL = 0
    const val HID_SENSOR = 1
    const val HID_VIEWER_KEYBOARD = 2

    // IDs for HID endpoints
    const val HID_VIEWER_CONTROL_INBOUND = 1
    const val HID_VIEWER_CONTROL_OUTBOUND = 0
    const val HID_VIEWER_KEYBOARD_INBOUND = 0
    const val HID_SENSOR_INBOUND = 0

    // IDs for HID Sensors
    const val SENSOR_ACCELEROMETER_ID = 1
    const val SENSOR_ALS_ID = 2
    const val SENSOR_GYRO_ID = 3
    const val SENSOR_MAGNETOMETER_ID = 4
    const val SENSOR_ORIENTATION_ID = 5

    // IDs for the Video Interfaces
    const val VIDEO_CONTROL = 0
    const val VIDEO_STREAM = 1
    const val VIDEO_HID = 2

    // IDs for Video Endpoints
    const val VIDEO_CONTROL_ENDPOINT_ONE = 0
    const val VIDEO_STREAM_ENDPOINT_ONE = 0
    const val VIDEO_HID_ENDPOINT_ONE = 0

    // IDs for Flashlight Commands
    const val FLASHLIGHT_ON = 9
    const val FLASHLIGHT_OFF = 10

    // IDs for the Outgoing Audio Interfaces
    const val MIC_CONTROL = 0
    const val MIC_STREAM = 1

    // IDs for the Outgoing Audio Interfaces
    const val MIC_STREAM_ENDPOINT_ONE = 0
    const val MIC_STREAM_ENDPOINT_TWO = 1

    // IDs for the Incoming Audio Interfaces
    const val AUDIO_CONTROL = 3
    const val AUDIO_STREAM_ONE = 4
    const val AUDIO_STREAM_TWO = 5

    // IDs for Outgoing Audio Endpoints

    // IDs for Incoming Audio Endpoints
    const val AUDIO_STREAM_ONE_ENDPOINT_ONE = 0
    const val AUDIO_STREAM_ONE_ENDPOINT_TWO = 1

    // Keyboard IDs / Command
    const val KEY_BACK = 28 // Enter
    const val KEY_BACK_LONG = 111 // Escape
    const val KEY_FRONT = 106 // Move Right
    const val KEY_FRONT_LONG = 103 // Move Up
    const val KEY_MIDDLE = 105 // Move Left
    const val KEY_MIDDLE_LONG = 108 // Move Down
    const val KEY_SIDE = 57 // Unknown
}