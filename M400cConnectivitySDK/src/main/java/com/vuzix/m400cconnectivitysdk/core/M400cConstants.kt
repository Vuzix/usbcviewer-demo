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
    /*const val SENSOR_ACCELEROMETER_ID = 1
    const val SENSOR_ALS_ID = 2
    const val SENSOR_GYRO_ID = 3
    const val SENSOR_MAGNETOMETER_ID = 4
    const val SENSOR_ORIENTATION_ID = 5*/
    const val SENSOR_NONE = 0
    const val SENSOR_ACCELEROMETER_ID = 1
    const val SENSOR_GYRO_ID = 2
    const val SENSOR_MAGNETOMETER_ID = 3
    const val SENSOR_ORIENTATION_ID = 4

    // IDs for the Video Interfaces
    const val VIDEO_CONTROL = 0
    const val VIDEO_STREAM = 1

    // IDs for Video Endpoints
    const val VIDEO_CONTROL_ENDPOINT_ONE = 0
    const val VIDEO_STREAM_ENDPOINT_ONE = 0


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

    // Keyboard IDs
    const val KEY_ONE = "Key One"
    const val KEY_ONE_LONG = "0000290000000000"
    const val KEY_TWO = "0000500000000000"
    const val KEY_TWO_LONG = "0000510000000000"
    const val KEY_THREE = "00004F0000000000"
    const val KEY_THREE_LONG = "0000520000000000"
    const val KEY_FOUR = "00006B0000000000"
    const val KEY_UP = "0000000000000000"
}