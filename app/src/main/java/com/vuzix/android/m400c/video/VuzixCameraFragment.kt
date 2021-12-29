package com.vuzix.android.m400c.video

import android.content.Context
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.Surface
import android.view.View
import android.view.View.OnKeyListener
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.vuzix.android.camerasdk.camera.UVCCameraHandler
import com.vuzix.android.camerasdk.ui.CameraDialog
import com.vuzix.android.camerasdk.ui.CameraFragment
import com.vuzix.android.camerasdk.ui.CameraViewInterface
import com.vuzix.android.camerasdk.usb.USBMonitor
import com.vuzix.android.camerasdk.usb.USBMonitor.OnDeviceConnectListener
import com.vuzix.android.camerasdk.usb.USBMonitor.UsbControlBlock
import com.vuzix.android.m400c.R
import com.vuzix.android.m400c.common.domain.entity.VuzixVideoDevice
import com.vuzix.android.m400c.core.util.DeviceUtil
import com.vuzix.android.m400c.core.util.M400cConstants
import com.vuzix.android.m400c.databinding.FragmentCameraDemoBinding
import timber.log.Timber

class VuzixCameraFragment : CameraFragment(), CameraDialog.CameraDialogParent, OnKeyListener {
    private val PREVIEW_WIDTH = 1920
    private val PREVIEW_HEIGHT = 1080
    private val PREVIEW_MODE = 1

    lateinit var usbMonitor: USBMonitor
    lateinit var cameraHandler: UVCCameraHandler
    lateinit var uvcCameraView: CameraViewInterface
    lateinit var onDeviceConnectListener: OnDeviceConnectListener
    lateinit var vuzixVideoDevice: VuzixVideoDevice
    lateinit var binding: FragmentCameraDemoBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_camera_demo, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        uvcCameraView = binding.uvcCameraView as CameraViewInterface
        uvcCameraView.setAspectRatio(PREVIEW_WIDTH, PREVIEW_HEIGHT)
        onDeviceConnectListener = object : OnDeviceConnectListener {
            override fun onAttach(device: UsbDevice?) {
                Timber.d("Usb Device Attached")
            }

            override fun onDetach(device: UsbDevice?) {
                Timber.d("Usb Device Detached")
            }

            override fun onConnect(
                device: UsbDevice?,
                ctrlBlock: UsbControlBlock?,
                createNew: Boolean
            ) {
                cameraHandler.open(ctrlBlock)
                startPreview()
            }

            override fun onDisconnect(device: UsbDevice?, ctrlBlock: UsbControlBlock?) {
                queueEvent({ cameraHandler.close() }, 0)
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
        val usbManager = requireContext().getSystemService(Context.USB_SERVICE) as UsbManager
        vuzixVideoDevice = DeviceUtil.getVideoDevice(usbManager)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        Timber.d("onStart")
        usbMonitor.register()
        usbMonitor.requestPermission(vuzixVideoDevice.usbDevice)
        uvcCameraView.onResume()
    }

    override fun onStop() {
        Timber.d("onStop")
        cameraHandler.close()
        uvcCameraView.onPause()
        super.onStop()
    }

    override fun onDestroy() {
        Timber.d("onDestroy")
        cameraHandler.release()
        usbMonitor.destroy()
        super.onDestroy()
    }

    fun startPreview() {
        cameraHandler.startPreview(Surface(uvcCameraView.surfaceTexture))
    }

    override fun getUSBMonitor(): USBMonitor {
        return usbMonitor
    }

    override fun onDialogResult(canceled: Boolean) {
        if (canceled) {
            requireActivity().onBackPressed()
        }
    }

    override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
        if (event?.keyCode == KeyEvent.KEYCODE_BACK) {
            requireActivity().onBackPressed()
        } else {
            when (event?.scanCode) {
                M400cConstants.KEY_ONE_LONG,
                M400cConstants.KEY_TWO_LONG,
                M400cConstants.KEY_THREE_LONG ->
                    if (event.action != KeyEvent.ACTION_UP) {
                        requireActivity().onBackPressed()
                    }
            }
            return true
        }
        return false
    }
}