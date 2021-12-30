package com.vuzix.android.m400c.video

import android.content.Context
import android.hardware.usb.UsbDeviceConnection
import android.hardware.usb.UsbManager
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnKeyListener
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.vuzix.android.m400c.R
import com.vuzix.android.m400c.common.domain.entity.VuzixVideoDevice
import com.vuzix.android.m400c.core.util.DeviceUtil
import com.vuzix.android.m400c.core.util.M400cConstants
import com.vuzix.android.m400c.core.util.strPrint
import com.vuzix.android.m400c.databinding.FragmentFlashlightDemoBinding
import timber.log.Timber

class FlashlightFragment : Fragment(), OnKeyListener {

    lateinit var binding: FragmentFlashlightDemoBinding
    lateinit var usbManager: UsbManager
    lateinit var videoDevice: VuzixVideoDevice
    lateinit var connection: UsbDeviceConnection
    lateinit var flashlightInterface: FlashlightInterface

    var state: FlashlightState = FlashlightState.Off

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        usbManager = requireContext().getSystemService(Context.USB_SERVICE) as UsbManager
        videoDevice = DeviceUtil.getVideoDevice(usbManager)
        flashlightInterface = videoDevice.usbDevice.let {
            val intf = it!!.getInterface(M400cConstants.VIDEO_HID)
            val endpoint = intf.getEndpoint(M400cConstants.VIDEO_HID_ENDPOINT_ONE)
            FlashlightInterface(intf, endpoint)
        }
        connection = usbManager.openDevice(videoDevice.usbDevice)
        connection.claimInterface(flashlightInterface.intf, true)
        Timber.d(connection.rawDescriptors.strPrint())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_flashlight_demo, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    fun turnFlashlightOn() {
        Timber.d("Turn On")
        binding.clFlashlightView.setBackgroundResource(R.drawable.bg_flashlight_on)
        state = FlashlightState.On
        val bytes = getFlashlightPacket(true)
        connection.controlTransfer(
            0x21,
            0x09,
            0x0200,
            flashlightInterface.intf.id,
            bytes,
            bytes.size,
            1000
        )
    }

    fun turnFlashlightOff() {
        Timber.d("Turn Off")
        binding.clFlashlightView.setBackgroundResource(R.drawable.bg_flashlight_off)
        state = FlashlightState.Off
        val bytes = getFlashlightPacket(false)
        connection.controlTransfer(
            0x21,
            0x0A,
            0x0200,
            flashlightInterface.intf.id,
            bytes,
            bytes.size,
            1000
        )
    }

    fun getFlashlightPacket(turnOn: Boolean): ByteArray {
        return if (turnOn) {
            byteArrayOf(M400cConstants.FLASHLIGHT_ON.toByte(), 0x01)
        } else {
            byteArrayOf(M400cConstants.FLASHLIGHT_OFF.toByte(), 0x01)
        }
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
                        when (state) {
                            FlashlightState.On -> turnFlashlightOff()
                            FlashlightState.Off -> turnFlashlightOn()
                        }
                    }
                }
                else -> requireActivity().onBackPressed()
            }
            return true
        }
        return false
    }
}