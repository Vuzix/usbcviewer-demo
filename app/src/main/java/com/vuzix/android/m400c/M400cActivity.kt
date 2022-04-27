package com.vuzix.android.m400c

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.KeyEvent
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.vuzix.android.m400c.audio.mic.MicrophoneFragment
import com.vuzix.android.m400c.audio.speakers.SpeakerFragment
import com.vuzix.android.m400c.hid.buttons.ButtonDemoFragment
import com.vuzix.android.m400c.hid.sensors.ArtificialHorizonFragment
import com.vuzix.android.m400c.video.camera.VuzixCameraFragment
import com.vuzix.android.m400c.video.flashlight.FlashlightFragment
import com.vuzix.sdk.usbcviewer.ColorMode
import com.vuzix.sdk.usbcviewer.ConnectionListener
import com.vuzix.sdk.usbcviewer.USBCDeviceManager
import com.vuzix.sdk.usbcviewer.utils.LogUtil
import timber.log.Timber

class M400cActivity : AppCompatActivity(),  ConnectionListener{

    private var hasDeviceAttached = false

    override fun dispatchKeyEvent(event: KeyEvent?): Boolean {

        val result = super.dispatchKeyEvent(event)
        if (result) {
            return result
        }

        Timber.d("$event")
        val currentFragment = NavHostFragment.findNavController(supportFragmentManager.primaryNavigationFragment!!).currentDestination
        when (currentFragment?.id) {
            R.id.sensorFragment -> {
                supportFragmentManager.fragments[0].let { fragment ->
                    fragment.childFragmentManager.fragments[0].let { it as ArtificialHorizonFragment
                        event?.let { keyEvent ->
                            it.onKey(it.view, keyEvent.keyCode, keyEvent)
                            return true
                        }
                    }
                }
            }
            R.id.microphoneFragment -> {
                supportFragmentManager.fragments[0].let { fragment ->
                    fragment.childFragmentManager.fragments[0].let { it as MicrophoneFragment
                        event?.let { keyEvent ->
                            it.onKey(it.view, keyEvent.keyCode, keyEvent)
                            return true
                        }
                    }
                }
            }
            R.id.speakerFragment -> {
                supportFragmentManager.fragments[0].let { fragment ->
                    fragment.childFragmentManager.fragments[0].let { it as SpeakerFragment
                        event?.let { keyEvent ->
                            it.onKey(it.view, keyEvent.keyCode, keyEvent)
                            return true
                        }
                    }
                }
            }
            R.id.cameraFragment -> {
                supportFragmentManager.fragments[0].let { fragment ->
                    fragment.childFragmentManager.fragments[0].let { it
                        if (it is VuzixCameraFragment) {
                            event?.let { keyEvent ->
                                it.onKey(it.view, keyEvent.keyCode, keyEvent)
                                return true
                            }
                        }
                    }
                }
            }
            R.id.flashlightFragment -> {
                supportFragmentManager.fragments[0].let { fragment ->
                    fragment.childFragmentManager.fragments[0].let { it as FlashlightFragment
                        event?.let { keyEvent ->
                            it.onKey(it.view, keyEvent.keyCode, keyEvent)
                            return true
                        }
                    }
                }
            }
            R.id.buttonDemoFragment -> {
                supportFragmentManager.fragments[0].let { fragment ->
                    fragment.childFragmentManager.fragments[0].let { it as ButtonDemoFragment
                        event?.let { keyEvent ->
                            it.onKey(it.view, keyEvent.keyCode, keyEvent)
                            return true
                        }
                    }
                }
            }


        }
        return result
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        hideSystemBars()
        setContentView(R.layout.activity_main)

        //ButtonManager.mapButtons(KeyEvent.KEYCODE_1, KeyEvent.KEYCODE_11, KeyEvent.KEYCODE_2, KeyEvent.KEYCODE_12, KeyEvent.KEYCODE_3, KeyEvent.KEYCODE_9)

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO),
                0
            )
        }

        val manager = USBCDeviceManager.shared(this)
        manager.registerDeviceMonitor(this)
        if (manager.isDeviceAvailable() && manager.deviceControlInterface != null) {
            tests()
        }

    }

    private fun hideSystemBars() {
        val windowInsetsController =
            ViewCompat.getWindowInsetsController(window.decorView) ?: return
        // Configure the behavior of the hidden system bars
        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        // Hide both the status bar and the navigation bar
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
    }

    // Some examples/playground of what you can do with the SDK
    fun tests() {
        val viewer = USBCDeviceManager.shared(this).deviceControlInterface
        // Restore defaults
//        viewer?.restoreDefaults()
//        LogUtil.debug("Defaults restored")
        // Test Brightness
//            val testValue = 255
//            val brightness = viewer?.getBrightness()
//            LogUtil.debug("getBrightness returned: $brightness")
//            val result = viewer?.setBrightness(testValue)
//            LogUtil.debug("setBrightness returned: $result")
//            val brightness2 = viewer?.getBrightness()
//            LogUtil.debug("getBrightness returned: $brightness2")
        // Force Left Eye
//        val result = viewer?.getForceLeftEye()
//        LogUtil.debug("getForceLeftEye returned: $result")
//
        // Auto ROTATE
//        viewer?.setAutoRotation(true)

        // VERSION
//            val version = viewer?.getVersion()
//            LogUtil.debug("getVersion: $version")
//
        // Button Tests
//        val buttonResults = viewer?.getButtonCodes(ButtonID.SIDE)
//        LogUtil.debug("getButtonCodes: $buttonResults") // 48, 49
//        var bc = viewer?.setButtonCodesWithAndroidKeyCodes(ButtonID.SIDE, KeyEvent.KEYCODE_A, KeyEvent.KEYCODE_W)
//        LogUtil.debug("setButtonCodes: $bc")
//        val buttonResults2 = viewer?.getButtonCodes(ButtonID.SIDE)
//        LogUtil.debug("getButtonCodes: $buttonResults2") // 48


        val version = viewer?.getVersion()
        LogUtil.debug("getVersion: ${version.toString()}")


        //CAMERA
//        USBCDeviceManager.shared(this).cameraInterface?.setFlashLight(false)
//        USBCDeviceManager.shared(this).cameraInterface?.setColorMode(ColorMode.NEGATIVE)

//            var def = USBCDeviceManager.shared(this).cameraInterface?.getDefaultValues()
//            LogUtil.debug("DEFAULTS: " + def.toString())
//
//            var current = USBCDeviceManager.shared(this).cameraInterface?.getCurrentValues()
//            LogUtil.debug("CURRENT VALUES: " + current.toString())


    }

    override fun onConnectionChanged(connected: Boolean) {
        if (!connected) {
            hasDeviceAttached = false
            findNavController(R.id.nav_host_fragment_container).popBackStack(R.id.m400cFragment, false)
            findNavController(R.id.nav_host_fragment_container).navigate(R.id.action_m400cFragment_to_connectDeviceFragment)
        }
        else {
            hasDeviceAttached = true
            findNavController(R.id.nav_host_fragment_container).popBackStack(R.id.m400cFragment, false)
        }
    }

    override fun onPermissionsChanged(granted: Boolean) {
        if (granted) {
            tests()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 0) {
            if (grantResults.size >= 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // preload camera permissions!
                USBCDeviceManager.shared(this).cameraInterface
            }
        }
    }

}