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
import com.vuzix.sdk.usbcviewer.ConnectionListener
import com.vuzix.sdk.usbcviewer.M400cConstants
import com.vuzix.sdk.usbcviewer.sensors.Sensors
import com.vuzix.sdk.usbcviewer.sensors.VuzixSensorEvent
import com.vuzix.sdk.usbcviewer.sensors.VuzixSensorListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ArtificialHorizonFragment :
    Fragment(R.layout.fragment_horizon_sensor_demo),
    OnKeyListener, VuzixSensorListener, ConnectionListener {

    private lateinit var mAltitudeIndicator: AltitudeIndicator
    private lateinit var ivCompassNumbers: ImageView
    lateinit var sensors: Sensors

    private val currAngle = FloatArray(3)
    private var accelValues = FloatArray(3)
    private var magValues = FloatArray(3)
    private var gyroValues = FloatArray(3)

    @RequiresApi(VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sensors = Sensors(requireContext(), this)
        sensors.registerDeviceMonitor(this)
        if (sensors.isDeviceAvailableAndAllowed()) {
            initConnect()
        }
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

    private fun initConnect() {
        try {
            sensors.connect()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun updateOrientation(orientationData: OrientationData) {
        currAngle[0] = orientationData.azimuth
        currAngle[1] = if (orientationData.pitch.isNaN()) 0f else orientationData.pitch
        currAngle[2] = if (orientationData.roll.isNaN()) 0f else orientationData.roll
        // We want the globe to do the opposite of what we're doing, if we're pitching
        // down to earth, roll the globe up so it looks like we're crashing.
        mAltitudeIndicator.setAttitude(
            -orientationData.pitch,
            -orientationData.roll
        )
        // On setRotation(), increasing values result in clockwise rotation. So if
        // our bearing is 90 degrees, we rotate the dial -90 to put East at the top.
        ivCompassNumbers.rotation = -orientationData.azimuth
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
        // todo: read left/right eye orientation from M400C
        val rotation = 0 // force to right-eye landscape orientation.
        updateOrientation(Orientation.updateOrientation(accelValues, magValues, rotation))
    }

    override fun onError(error: Exception) {
        GlobalScope.launch(Dispatchers.Main) {
            AlertDialog.Builder(requireContext())
                .setTitle("Error")
                .setMessage(error.message)
                .setNeutralButton("OK") { _, _ -> requireActivity().onBackPressed() }
                .show()
        }
    }

    override fun onSensorInitialized() {
        view?.invalidate()
    }

    override fun onConnectionChanged(connected: Boolean) {
        if (!connected) {
            GlobalScope.launch(Dispatchers.Main) {
                AlertDialog.Builder(requireContext())
                    .setTitle("No device found")
                    .setMessage("An M400-C device was not found. You will need to exit the app and connect the device before you can continue.")
                    .setNeutralButton("Okay") { _, _ -> requireActivity().finish() }
                    .show()
            }
        }
    }

    override fun onPermissionsChanged(granted: Boolean) {
        if (granted) {
            initConnect()
        }
    }
}
