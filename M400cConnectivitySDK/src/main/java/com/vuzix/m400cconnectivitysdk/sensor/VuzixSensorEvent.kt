package com.vuzix.m400cconnectivitysdk.sensor

class VuzixSensorEvent(
    /**
     * The Vuzix sensor that generated this event.
     */
    var sensor: VuzixSensor
) {
    var values: FloatArray? = null

    /**
     * The accuracy of this event.
     */
    var accuracy = 0

    /**
     * The time in nanosecond at which the event happened
     */
    var timestamp: Long = 0
}