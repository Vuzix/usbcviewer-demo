package com.vuzix.android.m400c

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.vuzix.android.m400c.video.camera.CameraSettingsFragment

class SettingsParentFragment: Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_main_settings, container, false)



        val toolbar: Toolbar = view.findViewById(R.id.toolbar2) as Toolbar
        (activity as AppCompatActivity?)?.setSupportActionBar(toolbar)
        (activity as AppCompatActivity?)?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity?)?.supportActionBar?.setHomeButtonEnabled(true)
        toolbar.setNavigationOnClickListener { view ->
            //requireActivity().onBackPressed()
            view.findNavController().popBackStack()
        }

        val type = arguments?.get("type") as Int?
        if (type == 0) {
            toolbar.title = "Settings"
            parentFragmentManager.beginTransaction()
                .replace(R.id.preference_frame, SettingsFragment()).commit()
        }
        else if (type == 1) {
            toolbar.title = "Camera Settings"
            parentFragmentManager.beginTransaction()
                .replace(R.id.preference_frame, CameraSettingsFragment()).commit()
        }

        return view
    }
}