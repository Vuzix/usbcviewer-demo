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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.google.android.material.button.MaterialButton
import com.vuzix.android.m400c.common.domain.entity.VuzixAudioDevice
import com.vuzix.android.m400c.common.domain.entity.VuzixHidDevice
import com.vuzix.android.m400c.common.domain.entity.VuzixVideoDevice
import com.vuzix.android.m400c.core.util.DeviceUtil
import com.vuzix.android.m400c.core.util.M400cConstants
import com.vuzix.android.m400c.databinding.FragmentMainBinding
import com.vuzix.android.m400c.databinding.FragmentMainNoFlashlightBinding
import timber.log.Timber

class M400cFragment : Fragment(), ActivityCompat.OnRequestPermissionsResultCallback {

    lateinit var usbManager: UsbManager

    lateinit var hidDevice: VuzixHidDevice

    lateinit var audioDevice: VuzixAudioDevice

    lateinit var videoDevice: VuzixVideoDevice

    lateinit var binding: FragmentMainNoFlashlightBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main_no_flashlight, container, false)
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
        hidDevice = DeviceUtil.getHidDevice(usbManager)
        hidDevice.usbDevice?.let {
            binding.btnDemoSensors.apply {
                setOnClickListener {
                    view.findNavController().navigate(R.id.action_m400cFragment_to_sensorFragment)
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
            checkPermission(it)
        } ?: run {
            binding.btnDemoSensors.isEnabled = false
            binding.btnDemoButtons.isEnabled = false
        }

        audioDevice = DeviceUtil.getAudioDevice(usbManager)
        audioDevice.usbDevice?.let {
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
            checkPermission(it)
        } ?: run {
            binding.btnDemoMic.isEnabled = false
            binding.btnDemoSpeakers.isEnabled = false
        }

        videoDevice = DeviceUtil.getVideoDevice(usbManager)
        videoDevice.usbDevice?.let {
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
            checkPermission(it)

        } ?: run {
            binding.btnDemoCamera.isEnabled = false
            binding.btnDemoFlashlight.isEnabled = false
        }
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

    private fun checkPermission(usbDevice: UsbDevice) {
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


}