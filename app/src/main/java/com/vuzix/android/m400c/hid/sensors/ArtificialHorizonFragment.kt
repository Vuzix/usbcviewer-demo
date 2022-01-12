package com.vuzix.android.m400c.hid.sensors

import android.content.Context
import android.hardware.usb.UsbManager
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.View.OnKeyListener
import androidx.fragment.app.Fragment
import com.vuzix.android.m400c.R
import com.vuzix.m400cconnectivitysdk.M400cConstants
import timber.log.Timber

class ArtificialHorizonFragment :
    Fragment(R.layout.fragment_horizon_sensor_demo),
    OnKeyListener, Orientation.Listener {

    lateinit var usbManager: UsbManager
    private lateinit var mAltitudeIndicator: AltitudeIndicator
    lateinit var mOrientation: Orientation

    private var azZero = 0.0f
    private var pitchZero = 0.0f
    private var rollZero = 180.0f
    private val currAngle = FloatArray(3)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        usbManager = requireContext().getSystemService(Context.USB_SERVICE) as UsbManager
        mOrientation = Orientation(usbManager)
        mOrientation.startListening(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        viewModel.startSensorStream()
        mAltitudeIndicator = view.findViewById(R.id.attitude_indicator)
        mOrientation.startListening(this)
    }

    override fun onStop() {
//        viewModel.stopSensorStream()
        mOrientation.stopListening()
        super.onStop()
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

    private fun setZero() {
        azZero = currAngle[0]
        pitchZero = currAngle[1]
        rollZero = currAngle[2]
    }

    override fun onOrientationChanged(azimuth: Float, pitch: Float, roll: Float) {
//        Timber.i("Orientation changed: az = $azimuth, pitch = $pitch, roll = $roll")
        currAngle[0] = azimuth
        currAngle[1] = if (pitch.isNaN()) 0f else pitch
        currAngle[2] = if (roll.isNaN()) 0f else roll
        mAltitudeIndicator.setAttitude(pitch - pitchZero, roll - rollZero)
    }
}
