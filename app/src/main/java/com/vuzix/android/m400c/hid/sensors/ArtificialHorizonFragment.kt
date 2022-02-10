package com.vuzix.android.m400c.hid.sensors

import android.hardware.Sensor
import android.os.Build
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.View.OnKeyListener
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.vuzix.android.m400c.R
import com.vuzix.sdk.usbcviewer.M400cConstants
import com.vuzix.sdk.usbcviewer.sensors.Sensors
import com.vuzix.sdk.usbcviewer.sensors.VuzixSensorEvent
import com.vuzix.sdk.usbcviewer.sensors.VuzixSensorListener
import timber.log.Timber

class ArtificialHorizonFragment :
    Fragment(R.layout.fragment_horizon_sensor_demo),
    OnKeyListener, VuzixSensorListener {

    private lateinit var mAltitudeIndicator: AltitudeIndicator
    private lateinit var ivCompassNumbers: ImageView
    lateinit var sensors: Sensors

    private var azZero = 0.0f
    private var pitchZero = 0.0f
    private var rollZero = 180.0f
    private val currAngle = FloatArray(3)
    private var accelValues = FloatArray(3)
    private var magValues = FloatArray(3)
    private var gyroValues = FloatArray(3)

    @RequiresApi(VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sensors = Sensors(requireContext(), this)
        try {
            sensors.connect()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        val metrics = requireActivity().windowManager.currentWindowMetrics
        Timber.d("${metrics.bounds.height()} x ${metrics.bounds.width()} ")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mAltitudeIndicator = view.findViewById(R.id.attitude_indicator)
        ivCompassNumbers = view.findViewById(R.id.iv_compass_numbers)
        if (sensors.connected) {
            sensors.initializeSensors(
                accelerometer = true,
                gyroscope = false,
                magnetometer = true,
                orientation = false
            )
        }
    }

    override fun onStop() {
        super.onStop()
        sensors.disconnect()
    }

    override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
        when (event?.keyCode) {
            KeyEvent.KEYCODE_BACK -> {
                requireActivity().onBackPressed()
            }
            KeyEvent.KEYCODE_ENTER -> {
                setZero()
            }
            else -> {
                when (event?.scanCode) {
                    M400cConstants.KEY_BACK_LONG,
                    M400cConstants.KEY_FRONT_LONG,
                    M400cConstants.KEY_MIDDLE_LONG ->
                        if (event.action != KeyEvent.ACTION_UP) {
                            requireActivity().onBackPressed()
                        }
                }
                return true
            }
        }
        return false
    }

    private fun updateOrientation(orientationData: OrientationData) {
        currAngle[0] = orientationData.azimuth
        currAngle[1] = if (orientationData.pitch.isNaN()) 0f else orientationData.pitch
        currAngle[2] = if (orientationData.roll.isNaN()) 0f else orientationData.roll
        mAltitudeIndicator.setAttitude(
            orientationData.pitch - pitchZero,
            orientationData.roll - rollZero
        )
        ivCompassNumbers.rotation = orientationData.azimuth - azZero
    }

    private fun setZero() {
        azZero = currAngle[0]
        pitchZero = currAngle[1]
        rollZero = currAngle[2]
    }

    override fun onSensorChanged(event: VuzixSensorEvent) {
        when (event.sensorType) {
            Sensor.TYPE_ACCELEROMETER -> {
                accelValues = event.values
            }
            Sensor.TYPE_MAGNETIC_FIELD -> {
                magValues = event.values
            }
            Sensor.TYPE_GYROSCOPE -> {
                gyroValues = event.values
            }
        }
        val rotation = if (Build.VERSION.SDK_INT < VERSION_CODES.R) {
            requireActivity().windowManager.defaultDisplay.rotation
        } else {
            requireContext().display?.rotation ?: 0
        }
        updateOrientation(Orientation.updateOrientation(accelValues, magValues, rotation))
    }

    override fun onError(error: Exception) {
        AlertDialog.Builder(requireContext())
            .setTitle("Error")
            .setMessage(error.message)
            .setNeutralButton("OK") { _, _ -> /* Do Nothing */ }
    }

    override fun onSensorInitialized() {
    }

}
