package com.vuzix.android.m400c

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.vuzix.android.m400c.databinding.FragmentConnectDeviceBindingLandImpl


class ConnectDeviceFragment: Fragment() {

    lateinit var binding: FragmentConnectDeviceBindingLandImpl

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_connect_device, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }
}