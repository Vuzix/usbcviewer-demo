package com.vuzix.android.m400c.video.camera

import android.os.Bundle
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SeekBarPreference
import androidx.preference.SwitchPreference
import com.vuzix.android.m400c.R
import com.vuzix.sdk.usbcviewer.*
import com.vuzix.sdk.usbcviewer.utils.toBoolean
import com.vuzix.sdk.usbcviewer.utils.toInt

class CameraSettingsFragment: PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.camera_settings, rootKey)

        val manager = USBCDeviceManager.shared(requireContext())

        val cameraExposurePref = findPreference<ListPreference>("camera_exposure_pref")
        val exposure = manager.cameraInterface?.getExposureCompensation()
        val values = resources.getStringArray(R.array.camera_exposure_values)
        if (exposure != null) {
            cameraExposurePref?.setDefaultValue(values[exposure.ordinal])
        }
        cameraExposurePref?.setOnPreferenceChangeListener { _, newValue ->
            val mode = Exposure.values()[(newValue as String).toInt()]
            manager.cameraInterface?.setExposureCompensation(mode)
            true
        }

        val priorityPref = findPreference<ListPreference>("priority_pref")
        val flicker = manager.cameraInterface?.getFlickerCancelPriority()
        val pValues = resources.getStringArray(R.array.priority_values)
        if (flicker != null) {
            priorityPref?.setDefaultValue(values[flicker.toInt()])
        }
        priorityPref?.setOnPreferenceChangeListener { _, newValue ->
            val mode = (newValue as String).toInt().toBoolean()
            manager.cameraInterface?.setFlickerCancelPriority(mode)
            true
        }

        val maxFrameRatePref = findPreference<SeekBarPreference>("frame_rate_pref")
        val frameRate = manager.cameraInterface?.getMaxFrameRate()
        maxFrameRatePref?.setDefaultValue(frameRate)
        maxFrameRatePref?.setOnPreferenceChangeListener { _, newValue ->
            manager.cameraInterface?.setMaxFrameRate(newValue as Int)
            true
        }


        val autoFocusPref = findPreference<ListPreference>("auto_focus_pref")
        val focus = manager.cameraInterface?.getAutoFocusMode()
        val afValues = resources.getStringArray(R.array.auto_focus_values)
        if (focus != null) {
            autoFocusPref?.setDefaultValue(afValues[focus.ordinal])
        }
        autoFocusPref?.setOnPreferenceChangeListener { _, newValue ->
            val mode = AutoFocusMode.values()[(newValue as String).toInt()]
            manager.cameraInterface?.setAutoFocusMode(mode)
            true
        }

        val noiseReduction = findPreference<SwitchPreference>("noise_reduction_pref")
        noiseReduction?.isChecked = (manager.cameraInterface?.getNoiseReductionMode() == NoiseReductionMode.AUTO)
        noiseReduction?.setOnPreferenceChangeListener { _, newValue ->
            if (newValue as? Boolean == true) {
                manager.cameraInterface?.setNoiseReductionToAuto()
            }
            else {
                val fixed = manager.cameraInterface?.getNoiseReductionStrength()
                if (fixed != null) {
                    manager.cameraInterface?.setNoiseReductionToFixed(fixed)
                }
            }
            true
        }

        val noiseReductionFixed = findPreference<SeekBarPreference>("noise_reduction_fixed_pref")
        val strength = manager.cameraInterface?.getNoiseReductionStrength()
        noiseReductionFixed?.setDefaultValue(strength)
        noiseReductionFixed?.setOnPreferenceChangeListener { _, newValue ->
            if (newValue is Int) {
                manager.cameraInterface?.setNoiseReductionToFixed(newValue)
            }
            true
        }

        val scanMode = findPreference<SwitchPreference>("scan_mode_pref")
        scanMode?.isChecked = manager.cameraInterface?.getScannerMode() == true
        scanMode?.setOnPreferenceChangeListener { _, newValue ->
            manager.cameraInterface?.setScannerMode(newValue as Boolean)
            true
        }

        val colorModePref = findPreference<ListPreference>("color_mode_pref")
        val colorMode = manager.cameraInterface?.getColorMode()?.first
        val cmValues = resources.getStringArray(R.array.color_mode_values)
        if (colorMode != null) {
            colorModePref?.setDefaultValue(cmValues[colorMode.ordinal])
        }
        colorModePref?.setOnPreferenceChangeListener { _, newValue ->
            val mode = ColorMode.values()[(newValue as String).toInt()]
            manager.cameraInterface?.setColorMode(mode)
            true
        }


        val jpegQualityPref = findPreference<SeekBarPreference>("jpeg_quality_pref")
        val quality = manager.cameraInterface?.getJpegQuality()
        jpegQualityPref?.setDefaultValue(if (quality?.first == true) 0 else quality?.second ?: 100 )
        jpegQualityPref?.setOnPreferenceChangeListener { _, newValue ->
            if (newValue is Int) {
                if (newValue < 13) {
                    manager.cameraInterface?.setJpegQualityToAuto()
                }
                else {
                    manager.cameraInterface?.setJpegQualityToManual(newValue)
                }
            }
            true
        }



        val flashModePref = findPreference<ListPreference>("flash_mode_pref")
        val flashValues = resources.getStringArray(R.array.flash_mode_values)
        flashModePref?.setDefaultValue("3") // OFF
        flashModePref?.setOnPreferenceChangeListener { _, newValue ->
            val value = (newValue as String).toInt()
            if (value is Int) {
                when(value) {
                    0 -> manager.cameraInterface?.setFlashAuto()
                    1-> manager.cameraInterface?.setFlashOn(false)
                    2-> manager.cameraInterface?.setFlashOff()
                }
            }
            true
        }


        val autoRotatePref = findPreference<SwitchPreference>("auto_rotate_pref")
        autoRotatePref?.isChecked = (manager.cameraInterface?.getAutoRotation() ?: false)
        autoRotatePref?.setOnPreferenceChangeListener { _, newValue ->
            manager.cameraInterface?.setAutoRotation(newValue as Boolean)
            true
        }

        val leftEyePref = findPreference<SwitchPreference>("left_eye_pref")
        leftEyePref?.isChecked = (manager.cameraInterface?.getForceLeftEye() ?: false)
        leftEyePref?.setOnPreferenceChangeListener { _, newValue ->
            manager.cameraInterface?.setForceLeftEye(newValue as Boolean)
            true
        }
    }
}