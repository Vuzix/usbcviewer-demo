package com.vuzix.android.m400c

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.google.android.material.button.MaterialButton
import com.vuzix.android.m400c.databinding.FragmentMainBinding
import com.vuzix.sdk.usbcviewer.M400cConstants
import timber.log.Timber

class M400cFragment : Fragment(), ActivityCompat.OnRequestPermissionsResultCallback {

    lateinit var usbManager: UsbManager

    lateinit var binding: FragmentMainBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            binding.btnDemoCamera.isEnabled = false
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO),
                0
            )
        }
        usbManager = requireContext().getSystemService(Context.USB_SERVICE) as UsbManager
        Timber.d(usbManager.deviceList.toString())
        if (usbManager.deviceList.isEmpty()) {
            AlertDialog.Builder(requireContext())
                .setTitle("No device found")
                .setMessage("An M400-C device was not found. You will need to exit the app and connect the device before you can continue.")
                .setNeutralButton("Okay") { _, _ -> requireActivity().finish() }
                .show()
        } else {
            binding.btnDemoSensors.apply {
                setOnClickListener {
                    view.findNavController().navigate(R.id.action_m400cFragment_to_sensorFragment)
//                        view.findNavController().navigate(R.id.action_m400cFragment_to_sensorDemoFragment)
                }
                setButtonFocusTheme(this)
            }
            binding.btnDemoButtons.apply {
                setOnClickListener {
                    view.findNavController()
                        .navigate(R.id.action_m400cFragment_to_buttonDemoFragment)
                }
                setButtonFocusTheme(this)
            }
            binding.btnDemoCamera.apply {
                setOnClickListener {
                    view.findNavController().navigate(R.id.action_m400cFragment_to_cameraFragment)
                }
                setButtonFocusTheme(this)
            }
            binding.btnDemoFlashlight.apply {
                setOnClickListener {
                    view.findNavController()
                        .navigate(R.id.action_m400cFragment_to_flashlightFragment)
                }
                setButtonFocusTheme(this)
            }
            binding.btnDemoMic.apply {
                setOnClickListener {
                    view.findNavController()
                        .navigate(R.id.action_m400cFragment_to_microphoneFragment)
                }
                setButtonFocusTheme(this)
            }
            binding.btnDemoSpeakers.apply {
                setOnClickListener {
                    view.findNavController().navigate(R.id.action_m400cFragment_to_speakerFragment)
                }
                setButtonFocusTheme(this)
            }
        }
        // Need to front-load permissions because the camera code doesn't like it when all
        // permissions aren't available.
        val videoDevice = getVideoDevice(usbManager)
        val audioDevice = getAudioDevice(usbManager)
        val hidDevice = getHidDevice(usbManager)
        checkPermission(videoDevice)
        checkPermission(audioDevice)
        checkPermission(hidDevice)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == 0) {
            if (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                binding.btnDemoCamera.isEnabled = true
            }
        }
    }

    private fun checkPermission(usbDevice: UsbDevice?) {
        usbManager.hasPermission(usbDevice).let {
            if (!it) {
                val usbPermissionIntent = PendingIntent.getBroadcast(
                    requireContext(),
                    0,
                    Intent(M400cConstants.ACTION_USB_PERMISSION),
                    PendingIntent.FLAG_IMMUTABLE
                )
                usbManager.requestPermission(usbDevice, usbPermissionIntent)
            }
        }
    }

    private fun setButtonFocusTheme(button: MaterialButton) {
        button.apply {
            setOnFocusChangeListener { v, hasFocus ->
                if (hasFocus) {
                    setBackgroundColor(Color.WHITE)
                    setTextColor(Color.BLACK)
                    setIconTintResource(R.color.black)
                } else {
                    setBackgroundColor(Color.TRANSPARENT)
                    setTextColor(Color.WHITE)
                    setIconTintResource(R.color.white)
                }
            }
        }
    }

    private fun getVideoDevice(usbManager: UsbManager): UsbDevice? {
        val devices = usbManager.deviceList
        return devices.values.firstOrNull { device -> device.productId == M400cConstants.VIDEO_PID && device.vendorId == M400cConstants.VIDEO_VID }
    }

    private fun getHidDevice(usbManager: UsbManager): UsbDevice? {
        val devices = usbManager.deviceList
        return devices.values.firstOrNull { device -> device.productId == M400cConstants.HID_PID && device.vendorId == M400cConstants.HID_VID }
    }

    private fun getAudioDevice(usbManager: UsbManager): UsbDevice? {
        val devices = usbManager.deviceList
        return devices.values.firstOrNull { device -> device.productId == M400cConstants.AUDIO_PID && device.vendorId == M400cConstants.AUDIO_VID }
    }
}