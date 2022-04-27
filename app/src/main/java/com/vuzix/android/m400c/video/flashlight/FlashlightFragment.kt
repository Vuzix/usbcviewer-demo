package com.vuzix.android.m400c.video.flashlight

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnKeyListener
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.vuzix.android.m400c.R
import com.vuzix.android.m400c.databinding.FragmentFlashlightDemoBinding
import com.vuzix.android.m400c.video.flashlight.FlashlightState.Off
import com.vuzix.android.m400c.video.flashlight.FlashlightState.On
import com.vuzix.sdk.usbcviewer.M400cConstants
import com.vuzix.sdk.usbcviewer.USBCDeviceManager
import timber.log.Timber

class FlashlightFragment : Fragment(), OnKeyListener {

    lateinit var binding: FragmentFlashlightDemoBinding
    var state: FlashlightState = Off

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_flashlight_demo, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.clFlashlightView.setOnClickListener {
            changeState()
        }
        return binding.root
    }

    override fun onStop() {
        super.onStop()

        val m400c = context?.let { USBCDeviceManager.shared(it) } ?: return
        m400c.cameraInterface?.setFlashLight(false)
    }


    override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
        Timber.d("onKey")
        if (event?.keyCode == KeyEvent.KEYCODE_BACK) {
            requireActivity().onBackPressed()
        } else {
            when (event?.scanCode) {
                M400cConstants.KEY_BACK,
                M400cConstants.KEY_FRONT,
                M400cConstants.KEY_MIDDLE,
                M400cConstants.KEY_SIDE -> {
                    if (event.action != KeyEvent.ACTION_UP) {
                        changeState()
                    }
                }
                else -> requireActivity().onBackPressed()
            }
            return true
        }
        return false
    }

    private fun changeState() {
        val m400c = context?.let { USBCDeviceManager.shared(it) } ?: return
        when (state) {
            Off -> {
                binding.clFlashlightView.setBackgroundResource(R.drawable.bg_flashlight_on)
                state = On
                m400c.cameraInterface?.setFlashLight(true)
            }
            On -> {
                binding.clFlashlightView.setBackgroundResource(R.drawable.bg_flashlight_off)
                state = Off
                m400c.cameraInterface?.setFlashLight(false)
            }
        }
    }
}