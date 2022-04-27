package com.vuzix.android.m400c.hid.sensors

import android.hardware.SensorManager
import android.view.Surface
import java.util.*
import kotlin.math.abs

object Orientation {

    private val accBuffer = LinkedList<FloatArray>()
    private val magBuffer = LinkedList<FloatArray>()

    private const val SMOOTHING_COUNT = 20

    init {
        for (i in 0 until SMOOTHING_COUNT) {
            accBuffer.add(floatArrayOf(0f, 0f, 0f))
            magBuffer.add(floatArrayOf(0f, 0f, 0f))
        }
    }

    fun updateOrientation(accelData: FloatArray, magData: FloatArray, rotation: Int): OrientationData {
        var rotationMatrix = FloatArray(9)
        val accelAvg = FloatArray(3)
        val magAvg = FloatArray(3)
        val worldAxisForDeviceAxisX: Int
        val worldAxisForDeviceAxisY: Int
        val adjustedRotationMatrix = FloatArray(9)

        when (rotation) {
            Surface.ROTATION_90 -> {
                worldAxisForDeviceAxisX = SensorManager.AXIS_Z
                worldAxisForDeviceAxisY = SensorManager.AXIS_MINUS_X
            }
            Surface.ROTATION_180 -> {
                worldAxisForDeviceAxisX = SensorManager.AXIS_MINUS_X
                worldAxisForDeviceAxisY = SensorManager.AXIS_MINUS_Z
            }
            Surface.ROTATION_270 -> {
                worldAxisForDeviceAxisX = SensorManager.AXIS_MINUS_Z
                worldAxisForDeviceAxisY = SensorManager.AXIS_X
            }
            else -> {
                worldAxisForDeviceAxisX = SensorManager.AXIS_X
                worldAxisForDeviceAxisY = SensorManager.AXIS_Z
            }
        }

        smoothOrientation(accelData, magData, accelAvg, magAvg)
        SensorManager.getRotationMatrix(rotationMatrix, null, accelAvg, magAvg)

        rotationMatrix = normalize(rotationMatrix)
        SensorManager.remapCoordinateSystem(rotationMatrix, worldAxisForDeviceAxisX, worldAxisForDeviceAxisY, adjustedRotationMatrix)
        val orientation = FloatArray(3)
        SensorManager.getOrientation(adjustedRotationMatrix, orientation)

        // Convert radians to degrees
        val azimuth = orientation[0] * 57
        val pitch = orientation[1] * 57
        val roll = orientation[2] * 57
        // Verbose debugging:
        //val pitchDir = if (pitch > 0) "down" else "up"
        //val bankDir = if (roll > 0) "right" else "left"
        //Log.d("Sensors", "Azimuth: $azimuth° Pitch: $pitch° (nose $pitchDir) Roll: $roll° (bank $bankDir) Device rotation: $rotation")
        return OrientationData(azimuth, pitch, roll)
    }

    private fun smoothOrientation(acc: FloatArray, mag: FloatArray, accelAvg: FloatArray, magAvg: FloatArray) {
        val a = floatArrayOf(0f, 0f, 0f)
        val m = floatArrayOf(0f, 0f, 0f)
        val accSize: Int = accBuffer.size
        val magSize: Int = magBuffer.size
        accBuffer.pollFirst()
        accBuffer.addLast(acc)
        magBuffer.pollFirst()
        magBuffer.addLast(mag)
        val accIter: Iterator<FloatArray> = accBuffer.iterator()
        while (accIter.hasNext()) {
            val tmp = accIter.next()
            a[0] += tmp[0]
            a[1] += tmp[1]
            a[2] += tmp[2]
        }
        val magIter: Iterator<FloatArray> = magBuffer.iterator()
        while (magIter.hasNext()) {
            val tmp = magIter.next()
            m[0] += tmp[0]
            m[1] += tmp[1]
            m[2] += tmp[2]
        }
        accelAvg[0] = a[0] / accSize
        accelAvg[1] = a[1] / accSize
        accelAvg[2] = a[2] / accSize
        magAvg[0] = m[0] / magSize
        magAvg[1] = m[1] / magSize
        magAvg[2] = m[2] / magSize
    }

    private fun normalize(data: FloatArray): FloatArray {
        val tmp = FloatArray(data.size)
        var max = 0f
        for (i in data.indices) {
            val t = abs(data[i])
            if (t > max) {
                max = t
            }
        }
        //float scale = 32677.0f / max;
        for (i in data.indices) {
            tmp[i] = data[i] / max
        }
        return tmp
    }
}