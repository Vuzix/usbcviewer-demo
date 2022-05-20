package com.vuzix.android.m400c.video.camera

import android.content.Context
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.os.Bundle
import android.view.*
import android.view.View.OnKeyListener
import android.widget.Button
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.vuzix.android.camerasdk.camera.UVCCameraHandler
import com.vuzix.android.camerasdk.ui.CameraDialog
import com.vuzix.android.camerasdk.ui.CameraFragment
import com.vuzix.android.camerasdk.ui.CameraViewInterface
import com.vuzix.android.camerasdk.usb.USBMonitor
import com.vuzix.android.camerasdk.usb.USBMonitor.OnDeviceConnectListener
import com.vuzix.android.camerasdk.usb.USBMonitor.UsbControlBlock
import com.vuzix.android.m400c.BuildConfig
import com.vuzix.android.m400c.R
import com.vuzix.android.m400c.databinding.FragmentCameraDemoBinding
import com.vuzix.sdk.usbcviewer.ColorMode
import com.vuzix.sdk.usbcviewer.M400cConstants
import com.vuzix.sdk.usbcviewer.USBCDeviceManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber

class VuzixCameraFragment : CameraFragment(), CameraDialog.CameraDialogParent, OnKeyListener {
    private val PREVIEW_WIDTH = 1920
    private val PREVIEW_HEIGHT = 1080
    private val PREVIEW_MODE = 1

    lateinit var onDeviceConnectListener: OnDeviceConnectListener
    lateinit var binding: FragmentCameraDemoBinding
    lateinit var usbManager: UsbManager

    var uvcCameraView: CameraViewInterface? = null
    var usbMonitor: USBMonitor? = null
    var cameraHandler: UVCCameraHandler? = null
    var vuzixVideoDevice: UsbDevice? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        usbManager = requireContext().getSystemService(Context.USB_SERVICE) as UsbManager
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_camera_demo, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        uvcCameraView = binding.uvcCameraView as CameraViewInterface
        uvcCameraView?.setAspectRatio(PREVIEW_WIDTH, PREVIEW_HEIGHT)
        onDeviceConnectListener = object : OnDeviceConnectListener {
            override fun onAttach(device: UsbDevice?) {
                Timber.d("Usb Device Attached: ${device?.deviceId}")
            }

            override fun onDetach(device: UsbDevice?) {
                Timber.d("Usb Device Detached")
            }

            override fun onConnect(
                device: UsbDevice?,
                ctrlBlock: UsbControlBlock?,
                createNew: Boolean
            ) {
                cameraHandler?.open(ctrlBlock)
                startPreview()
            }

            override fun onDisconnect(device: UsbDevice?, ctrlBlock: UsbControlBlock?) {
                queueEvent({ cameraHandler?.close() }, 0)
            }

            override fun onCancel(device: UsbDevice?) {
                // Nothing
            }

        }
        usbMonitor = USBMonitor(requireContext(), onDeviceConnectListener)
        cameraHandler = UVCCameraHandler.createHandler(
            requireActivity(),
            uvcCameraView,
            1,
            PREVIEW_WIDTH,
            PREVIEW_HEIGHT,
            PREVIEW_MODE
        )
        vuzixVideoDevice = getVideoDevice(usbManager)


        val view = binding.root
        val button = view.findViewById<Button>(R.id.camera_settings)
        button.setOnClickListener {
            val bundle = Bundle()
            bundle.putInt("type", 1)
            view.findNavController().navigate(R.id.action_cameraFragment_to_settingsParentFragment, bundle)
        }
        button.visibility = if (BuildConfig.DEBUG) View.VISIBLE else View.INVISIBLE

        return view
    }

    override fun onStart() {
        super.onStart()
        Timber.d("onStart")
        usbMonitor?.register()
        usbMonitor?.requestPermission(vuzixVideoDevice)
        uvcCameraView?.onResume()
    }

    override fun onStop() {
        Timber.d("onStop")
        cameraHandler?.close()
        uvcCameraView?.onPause()
        //turnOffLed()
        GlobalScope.launch(Dispatchers.Main) { binding.pbCamera?.isVisible = false }
        super.onStop()
    }

    override fun onDestroy() {
        Timber.d("onDestroy")
        cameraHandler?.release()
        usbMonitor?.destroy()
        uvcCameraView = null
        usbMonitor = null
        cameraHandler = null
        super.onDestroy()
    }

    fun startPreview() {
        GlobalScope.launch(Dispatchers.Main) { binding.pbCamera?.isVisible = true }
        uvcCameraView?.let {
            while (true) {
                if (it.hasSurface()) {
                    break
                }
                Thread.sleep(50)
            }
            cameraHandler?.startPreview(Surface(it.surfaceTexture))
        }
    }

    override fun getUSBMonitor(): USBMonitor {
        return usbMonitor!!
    }

    override fun onDialogResult(canceled: Boolean) {
        if (canceled) {
            requireActivity().onBackPressed()
        }
    }

    override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
        val m400c = context?.let { USBCDeviceManager.shared(it) } ?: return false

        when (event?.keyCode) {
            KeyEvent.KEYCODE_BACK -> {
                requireActivity().onBackPressed()
            }
            KeyEvent.KEYCODE_DPAD_RIGHT -> {
                m400c.cameraInterface?.setColorModeToBlackAndWhiteWithThreshold(70)
            }
            KeyEvent.KEYCODE_DPAD_LEFT -> {
                m400c.cameraInterface?.setColorMode(ColorMode.COLOR)
            }
            KeyEvent.KEYCODE_ENTER -> {
                m400c.cameraInterface?.setColorMode(ColorMode.NEGATIVE)
            }
            else -> {
                when (event?.scanCode) {
                    M400cConstants.KEY_BACK_LONG,
                    M400cConstants.KEY_FRONT_LONG,
                    M400cConstants.KEY_MIDDLE_LONG ->
                        if (event.action != KeyEvent.ACTION_UP) {
                            requireActivity().onBackPressed()
                        }
                }
                return true
            }
        }
        return false
    }

    private fun getVideoDevice(usbManager: UsbManager): UsbDevice? {
        val devices = usbManager.deviceList
        return devices.values.firstOrNull { device -> device.productId == M400cConstants.CAMERA_PID && device.vendorId == M400cConstants.CAMERA_VID }
    }
}