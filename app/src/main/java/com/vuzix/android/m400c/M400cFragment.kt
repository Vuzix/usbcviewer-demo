package com.vuzix.android.m400c

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.camera2.CameraManager
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.vuzix.android.m400c.common.domain.entity.VuzixAudioDevice
import com.vuzix.android.m400c.common.domain.entity.VuzixHidDevice
import com.vuzix.android.m400c.common.domain.entity.VuzixVideoDevice
import com.vuzix.android.m400c.core.util.M400cConstants
import com.vuzix.android.m400c.databinding.FragmentMainAltBinding
import com.vuzix.android.m400c.databinding.FragmentMainBinding
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class M400cFragment : Fragment(), ActivityCompat.OnRequestPermissionsResultCallback {

    @Inject
    lateinit var usbManager: UsbManager

    @Inject
    lateinit var hidDevice: VuzixHidDevice

    @Inject
    lateinit var audioDevice: VuzixAudioDevice

    @Inject
    lateinit var videoDevice: VuzixVideoDevice

    lateinit var binding: FragmentMainAltBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main_alt, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Timber.d(usbManager.deviceList.toString())
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            binding.btnDemoCamera.isEnabled = false
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.CAMERA), 0)
        }
        hidDevice.usbDevice?.let {
            binding.btnDemoSensors.setOnClickListener {
                view.findNavController().navigate(R.id.action_m400cFragment_to_sensorFragment)
            }
            binding.btnDemoButtons.setOnClickListener {
                view.findNavController().navigate(R.id.action_m400cFragment_to_buttonDemoFragment)
            }
            checkPermission(it)
        } ?: run {
            binding.btnDemoSensors.isEnabled = false
            binding.btnDemoButtons.isEnabled = false
        }

        audioDevice.usbDevice?.let {
            binding.btnDemoMic.setOnClickListener {
                view.findNavController().navigate(R.id.action_m400cFragment_to_microphoneFragment)
            }
            binding.btnDemoSpeakers.setOnClickListener {
                view.findNavController().navigate(R.id.action_m400cFragment_to_speakerFragment)
            }
            checkPermission(it)
        } ?: run {
            binding.btnDemoMic.isEnabled = false
            binding.btnDemoSpeakers.isEnabled = false
        }

        videoDevice.usbDevice?.let {
            binding.btnDemoCamera.setOnClickListener {
                view.findNavController().navigate(R.id.action_m400cFragment_to_cameraFragment)
            }
            checkPermission(it)
        } ?: run {
            binding.btnDemoCamera.isEnabled = false
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
                    0
                )
                usbManager.requestPermission(usbDevice, usbPermissionIntent)
            }
        }
    }


}