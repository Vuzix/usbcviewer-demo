package com.vuzix.android.m400c

import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SeekBarPreference
import androidx.preference.SwitchPreference
import com.vuzix.sdk.usbcviewer.TouchPadSettings
import com.vuzix.sdk.usbcviewer.USBCDeviceManager
import com.vuzix.sdk.usbcviewer.utils.LogUtil


class SettingsFragment: PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.main_settings, rootKey)
        setupInitialValues()
    }

    fun setupInitialValues() {
        val manager = USBCDeviceManager.shared(requireContext())

        try {

            val brightness = findPreference<SeekBarPreference>("brightnessPref")
            brightness?.value =
                (manager.deviceControlInterface?.getBrightness()?.times(100) ?: 128) / 255
            brightness?.setOnPreferenceChangeListener { _, newValue ->
                val newBrightness = ((newValue as Int).times(255)) / 100
                manager.deviceControlInterface?.setBrightness(newBrightness)
                return@setOnPreferenceChangeListener true
            }

            val autoRotate = findPreference<SwitchPreference>("autoRotatePref")
            val forceLeftEye = findPreference<SwitchPreference>("forceLeftEyePref")
            val autoRotateValue = (manager.deviceControlInterface?.getAutoRotation() ?: false)
            autoRotate?.isChecked = autoRotateValue
            autoRotate?.setOnPreferenceChangeListener { _, newValue ->
                manager.deviceControlInterface?.setAutoRotation(newValue as Boolean)
                forceLeftEye?.isEnabled = !(newValue as Boolean)
                true
            }

            forceLeftEye?.isEnabled = autoRotateValue == false
            forceLeftEye?.isChecked = (manager.deviceControlInterface?.getForceLeftEye() ?: false)
            forceLeftEye?.setOnPreferenceChangeListener { _, newValue ->
                manager.deviceControlInterface?.setForceLeftEye(newValue as Boolean)
                true
            }


            val hToggle = findPreference<SwitchPreference>("hTogglePref")
            val vToggle = findPreference<SwitchPreference>("vTogglePref")
            val enableTouchPadPref = findPreference<SwitchPreference>("enableTouchPadPref")
            var touchPadEnabled = (manager.touchPadInterface?.getTouchPadEnabled() ?: true)
            enableTouchPadPref?.isChecked = touchPadEnabled
            enableTouchPadPref?.setOnPreferenceChangeListener { _, newValue ->
                manager.touchPadInterface?.setTouchPadEnabled(newValue as Boolean)
                touchPadEnabled = newValue as Boolean
                hToggle?.isEnabled = touchPadEnabled
                vToggle?.isEnabled = touchPadEnabled

                    val touchpadPrefs = manager.touchPadInterface?.getTouchPadOrientation()
                    LogUtil.debug(touchpadPrefs.toString())
                    touchpadPrefs?.let {
                        hToggle?.isChecked = (it.horizontalDirectionRight)
                        vToggle?.isChecked = (it.verticalDirectionUP)
                    }


                true
            }

            hToggle?.isEnabled = touchPadEnabled
            vToggle?.isEnabled = touchPadEnabled
            val touchpadPrefs = manager.touchPadInterface?.getTouchPadOrientation()
            touchpadPrefs?.let {
                hToggle?.isChecked = (it.horizontalDirectionRight)
                vToggle?.isChecked = (it.verticalDirectionUP)
            }

            val buttonPrefListener: Preference.OnPreferenceChangeListener =
                Preference.OnPreferenceChangeListener { preference, newValue ->
                    if (touchpadPrefs != null) {
                        val newSettings = TouchPadSettings(
                            if (preference == hToggle) newValue as Boolean else touchpadPrefs.horizontalDirectionRight,
                            if (preference == vToggle) newValue as Boolean else touchpadPrefs.verticalDirectionUP,
                            touchpadPrefs.scrollDirectionUp,
                            touchpadPrefs.panDirectionRight,
                            touchpadPrefs.zoomDirectionIn
                        )
                        manager.touchPadInterface?.setTouchPadOrientation(newSettings)
                    }
                    true
                }
            hToggle?.onPreferenceChangeListener = buttonPrefListener
            vToggle?.onPreferenceChangeListener = buttonPrefListener

            val restoreDefaults = findPreference<Preference>("defaults_pref")

            restoreDefaults?.setOnPreferenceClickListener {
                manager.deviceControlInterface?.restoreDefaults()
                // reload
                setupInitialValues()
                true
            }
        }
        catch (e: Exception) {
            LogUtil.debug(e.toString())
        }
    }


}