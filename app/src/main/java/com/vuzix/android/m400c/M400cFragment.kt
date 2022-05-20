package com.vuzix.android.m400c

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
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
import com.vuzix.android.m400c.databinding.FragmentMainBinding

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
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO),
                0
            )
        }
        usbManager = requireContext().getSystemService(Context.USB_SERVICE) as UsbManager
        if (usbManager.deviceList.isEmpty()) {
            view.findNavController().navigate(R.id.action_m400cFragment_to_connectDeviceFragment)
        } else {
            binding.btnDemoSensors?.apply {
                setOnClickListener {
                    view.findNavController().navigate(R.id.action_m400cFragment_to_sensorFragment)
                }
                setButtonFocusTheme(this)
            }
            binding.btnDemoButtons?.apply {
                setOnClickListener {
                    view.findNavController()
                        .navigate(R.id.action_m400cFragment_to_buttonDemoFragment)
                }
                setButtonFocusTheme(this)
            }
            binding.btnDemoCamera?.apply {
                setOnClickListener {
                    view.findNavController().navigate(R.id.action_m400cFragment_to_cameraFragment)
                }
                setButtonFocusTheme(this)
            }
            binding.btnDemoFlashlight?.apply {
                setOnClickListener {
                    view.findNavController()
                        .navigate(R.id.action_m400cFragment_to_flashlightFragment)
                }
                setButtonFocusTheme(this)
            }
            binding.btnDemoMic?.apply {
                setOnClickListener {
                    view.findNavController()
                        .navigate(R.id.action_m400cFragment_to_microphoneFragment)
                }
                setButtonFocusTheme(this)
            }
            binding.btnDemoSpeakers?.apply {
                setOnClickListener {
                    view.findNavController().navigate(R.id.action_m400cFragment_to_speakerFragment)
                }
                setButtonFocusTheme(this)
            }
            binding.btnDemoSettings?.apply {
                setOnClickListener {
                    val bundle = Bundle()
                    bundle.putInt("type", 0)
                    view.findNavController().navigate(R.id.action_m400cFragment_to_settingsParentFragment, bundle)
                }
            }

            binding.btnDemoButtons?.visibility = if (BuildConfig.DEBUG) View.VISIBLE else View.INVISIBLE
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