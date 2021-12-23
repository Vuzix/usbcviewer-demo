package com.vuzix.android.m400c.video

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.vuzix.android.m400c.R
import com.vuzix.android.m400c.databinding.FragmentFlashlightDemoBinding

class FlashlightFragment : Fragment() {

    lateinit var binding: FragmentFlashlightDemoBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_flashlight_demo, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }
}