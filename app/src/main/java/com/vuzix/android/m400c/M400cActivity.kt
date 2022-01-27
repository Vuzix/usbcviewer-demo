package com.vuzix.android.m400c

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.os.Bundle
import android.view.KeyEvent
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.fragment.NavHostFragment
import com.vuzix.android.m400c.audio.mic.MicrophoneFragment
import com.vuzix.android.m400c.audio.speakers.SpeakerFragment
import com.vuzix.android.m400c.hid.buttons.ButtonDemoFragment
import com.vuzix.android.m400c.hid.sensors.ArtificialHorizonFragment
import com.vuzix.android.m400c.video.flashlight.FlashlightFragment
import com.vuzix.android.m400c.video.camera.VuzixCameraFragment
import com.vuzix.sdk.usbcviewer.M400cConstants
import timber.log.Timber

class M400cActivity : AppCompatActivity() {

    override fun dispatchKeyEvent(event: KeyEvent?): Boolean {
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
                    fragment.childFragmentManager.fragments[0].let { it as VuzixCameraFragment
                        event?.let { keyEvent ->
                            it.onKey(it.view, keyEvent.keyCode, keyEvent)
                            return true
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
        return super.dispatchKeyEvent(event)
    }

    private val usbReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            Timber.d("${intent.action}")
            if (M400cConstants.ACTION_USB_PERMISSION == intent.action) {
                synchronized(this) {
                    val device: UsbDevice? = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE)

                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        device?.apply {
                            //call method to set up device communication
                        }
                    } else {
                        Timber.d("permission denied for device $device")
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        hideSystemBars()
        setContentView(R.layout.activity_main)
        val filter = IntentFilter(M400cConstants.ACTION_USB_PERMISSION)
        registerReceiver(usbReceiver, filter)
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
}