package com.vuzix.android.m400c.hid.presentation.sensors

import android.content.Context
import android.hardware.usb.UsbManager
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.View.OnKeyListener
import androidx.fragment.app.viewModels
import com.vuzix.android.m400c.R
import com.vuzix.android.m400c.core.base.BaseFragment
import com.vuzix.android.m400c.core.util.M400cConstants
import com.vuzix.android.m400c.databinding.FragmentHorizonSensorDemoBinding
import com.vuzix.android.m400c.hid.presentation.sensors.SensorAction.Default
import com.vuzix.android.m400c.hid.presentation.sensors.SensorAction.Loading
import com.vuzix.m400cconnectivitysdk.core.VuzixSensor
import com.vuzix.m400cconnectivitysdk.core.VuzixSensorEvent
import timber.log.Timber

class ArtificialHorizonFragment :
    BaseFragment<SensorUiState, HorizonViewModel, FragmentHorizonSensorDemoBinding>(R.layout.fragment_horizon_sensor_demo),
    OnKeyListener, Orientation.Listener {
    override val viewModel: HorizonViewModel by viewModels() {
        HorizonViewModelFactory()
    }

    lateinit var usbManager: UsbManager
    private lateinit var mAttitudeIndicator: AttitudeIndicator
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
        mAttitudeIndicator = view.findViewById(R.id.attitude_indicator)
        if (mOrientation != null) {
            mOrientation.startListening(this)
        }
    }

    override fun onStop() {
//        viewModel.stopSensorStream()
        if (mOrientation != null) {
            mOrientation.stopListening()
        }
        super.onStop()
    }

    override fun onUiStateUpdated(uiState: SensorUiState) {
        var event = VuzixSensorEvent(VuzixSensor())
        var array = FloatArray(4)
        when (uiState.action) {
            is Default -> {
                // Do nothing
            }
            //is Error -> binding.tvHidMessage?.text = uiState.action.errorMessage
            is Loading -> {
                // Do nothing for now (may not need this)
            }
        }
    }

    override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
        if (event?.keyCode == KeyEvent.KEYCODE_BACK) {
            requireActivity().onBackPressed()
        } else if (event?.keyCode == KeyEvent.KEYCODE_ENTER) {
            setZero()
        } else {
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
        return false
    }

    private fun setZero() {
        azZero = currAngle[0]
        pitchZero = currAngle[1]
        rollZero = currAngle[2]
    }

    override fun onOrientationChanged(azimuth: Float, pitch: Float, roll: Float) {
        var pitch = pitch
        var roll = roll
        Timber.i("Orientation changed: az = $azimuth, pitch = $pitch, roll = $roll")
        if (java.lang.Float.isNaN(pitch)) pitch = 0f
        if (java.lang.Float.isNaN(roll)) roll = 0f
        currAngle[0] = azimuth
        currAngle[1] = pitch
        currAngle[2] = roll
        mAttitudeIndicator.setAttitude(pitch - pitchZero, roll - rollZero)
    }
}
