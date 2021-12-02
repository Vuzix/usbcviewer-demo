package com.vuzix.android.m400c.common.presentation

import android.hardware.usb.UsbInterface
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.vuzix.android.m400c.databinding.ListDeviceInfoBinding

class DeviceViewHolder(private val binding: ListDeviceInfoBinding) : ViewHolder(binding.root) {

    fun bind(usbInterface: UsbInterface) {

        binding.tvInterfaceName.text = "${usbInterface.name} ${usbInterface.id}"
        binding.tvEndpointOne.apply {
            text = when (usbInterface.endpointCount) {
                0 -> "None"
                1, 2 -> "${usbInterface.getEndpoint(0)} | ${usbInterface.getEndpoint(0).direction}"
                else -> "None"
            }
        }

        binding.tvEndpointTwo.apply {
            when (usbInterface.endpointCount) {
                0, 1 -> isVisible = false
                2 -> text = "${usbInterface.getEndpoint(1)} | ${usbInterface.getEndpoint(1).direction}"
                else -> {}
            }
        }
    }
}