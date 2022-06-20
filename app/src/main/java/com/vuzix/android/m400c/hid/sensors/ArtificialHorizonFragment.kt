package com.vuzix.android.m400c.hid.sensors

import android.hardware.Sensor
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.view.KeyEvent
import android.view.Surface
import android.view.View
import android.view.View.OnKeyListener
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.vuzix.android.m400c.R
import com.vuzix.sdk.usbcviewer.M400cConstants
import com.vuzix.sdk.usbcviewer.USBCDeviceManager
import com.vuzix.sdk.usbcviewer.sensors.VuzixSensorEvent
import com.vuzix.sdk.usbcviewer.sensors.VuzixSensorListener
import com.vuzix.sdk.usbcviewer.utils.LogUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ArtificialHorizonFragment :
    Fragment(R.layout.fragment_horizon_sensor_demo),
    OnKeyListener, VuzixSensorListener {

    private lateinit var mAltitudeIndicator: AltitudeIndicator
    private lateinit var ivCompassNumbers: ImageView

    private val currAngle = FloatArray(3)
    private var accelValues = FloatArray(3)
    private var magValues = FloatArray(3)
    private var gyroValues = FloatArray(3)

    @RequiresApi(VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context?.let { USBCDeviceManager.shared(it).sensorInterface?.registerListener(this) };
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mAltitudeIndicator = view.findViewById(R.id.attitude_indicator)
        ivCompassNumbers = view.findViewById(R.id.iv_compass_numbers)

        try {
            context?.let {
                //M400cUsbManager.shared(it).sensorInterface?.startUpdatingSensor(Sensor.TYPE_ROTATION_VECTOR)
                USBCDeviceManager.shared(it).sensorInterface?.startUpdatingSensor(Sensor.TYPE_MAGNETIC_FIELD)
                USBCDeviceManager.shared(it).sensorInterface?.startUpdatingSensor(Sensor.TYPE_ACCELEROMETER)
            }
        } catch (e: Exception) {
            //nothing.
        }
    }

    override fun onStop() {
        super.onStop()
        context?.let { USBCDeviceManager.shared(it).sensorInterface?.unregisterListener(this) };
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

    var rotation = Surface.ROTATION_0 // default to right-eye landscape orientation.
    var outputEveryNth = 0
    override fun onSensorChanged(event: VuzixSensorEvent) {

        when (event.sensorType) {
            Sensor.TYPE_ACCELEROMETER -> {
                accelValues = event.values
                val new_rotation = if (accelValues[1] > 0) {
                    Surface.ROTATION_0
                } else {
                    Surface.ROTATION_180
                }
                if (rotation != new_rotation) {
                    rotation = new_rotation
                    LogUtil.rel("Setting rotation to $rotation")
                }
            }
            Sensor.TYPE_MAGNETIC_FIELD -> {
                magValues = event.values
            }
            Sensor.TYPE_GYROSCOPE -> {
                gyroValues = event.values
            }
//            Sensor.TYPE_ROTATION_VECTOR -> {
//                val rotationVector = event.values
//
//                val z = rotationVector[2]
//                rotation = if (z > 0.5) {  // <--same logic as firmware.
//                    // left eye
//                    2
//                } else {
//                    0
//                }
//                LogUtil.debug("r[0] = ${rotationVector[0]}, r[1] = ${rotationVector[1]}, r[2] = ${rotationVector[2]}, r[3] = ${rotationVector[3]}")
//
//            }
        }
        if (outputEveryNth > 100) {
            LogUtil.debug("onSensorChanged EVENT **************************")
            LogUtil.debug("time stamp: ${event.timestamp}")
            LogUtil.debug("accelValues[0] = ${accelValues[0]}, accelValues[1] = ${accelValues[1]}, accelValues[2] = ${accelValues[2]}")
            LogUtil.debug("magValues[0] = ${magValues[0]}, magValues[1] = ${magValues[1]}, magValues[2] = ${magValues[2]}")
            LogUtil.debug("***********************************************")
            outputEveryNth = 0
        }
        outputEveryNth++
        updateOrientation(Orientation.updateOrientation(accelValues, magValues, rotation))
    }

    override fun onError(error: Exception) {
        GlobalScope.launch(Dispatchers.Main) {
            AlertDialog.Builder(requireContext())
                .setTitle("Error")
                .setMessage(error.message)
                .setNeutralButton("OK") { _, _ ->
                    var activity = activity
                    activity?.onBackPressed()
                }
                .show()
        }
    }

    override fun onSensorInitialized() {
        view?.invalidate()
    }

}
