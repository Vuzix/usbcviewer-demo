package com.vuzix.android.m400c

import android.content.Context
import android.hardware.usb.UsbManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.vuzix.android.m400c.databinding.FragmentSplashBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class M400cSplashFragment : Fragment() {

    lateinit var usbManager: UsbManager
    lateinit var binding: FragmentSplashBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_splash, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        usbManager = requireContext().getSystemService(Context.USB_SERVICE) as UsbManager
        if (usbManager.deviceList.isEmpty()) {
            AlertDialog.Builder(requireContext())
                .setTitle("No device found")
                .setMessage("An M400-C device was not found. You will need to exit the app and connect the device before you can continue.")
                .setNeutralButton("Okay") { _, _ -> requireActivity().finish() }
                .show()
        } else {
            GlobalScope.launch {
                delay(2000)
                launch(Dispatchers.Main) {
                    view.findNavController().navigate(R.id.action_m400cSplashFragment_to_m400cFragment)
                }
            }
        }
    }
}