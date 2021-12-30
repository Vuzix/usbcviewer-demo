package com.vuzix.m400cconnectivitysdk.core

interface VuzixSensorEventListener {
    fun onSensorChanged(sensorEvent: VuzixSensorEvent)

    fun onAccuracyChanged(vuzixSensor: VuzixSensor, accuracy: Int)
}