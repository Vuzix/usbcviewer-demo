package com.vuzix.m400cconnectivitysdk.sensor

interface VuzixSensorEventListener {
    fun onSensorChanged(sensorEvent: VuzixSensorEvent)

    fun onAccuracyChanged(vuzixSensor: VuzixSensor, accuracy: Int)
}