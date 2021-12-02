package com.vuzix.android.m400c.common.presentation

import android.hardware.usb.UsbInterface
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.vuzix.android.m400c.databinding.ListDeviceInfoBinding

class DeviceAdapter(private val usbInterfaceList: List<UsbInterface>) : Adapter<DeviceViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceViewHolder {
        val binding = ListDeviceInfoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DeviceViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DeviceViewHolder, position: Int) {
        holder.bind(usbInterfaceList[position])
    }

    override fun getItemCount(): Int = usbInterfaceList.size
}