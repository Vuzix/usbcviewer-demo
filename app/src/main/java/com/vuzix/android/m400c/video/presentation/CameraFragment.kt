package com.vuzix.android.m400c.video.presentation

import android.app.PendingIntent
import android.content.Intent
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.vuzix.android.camerasdk.UVCCameraProxy
import com.vuzix.android.camerasdk.callbacks.ConnectCallback
import com.vuzix.android.camerasdk.enums.PicturePath.APPCACHE
import com.vuzix.android.m400c.R
import com.vuzix.android.m400c.common.domain.entity.VuzixVideoDevice
import com.vuzix.android.m400c.core.base.BaseFragment
import com.vuzix.android.m400c.core.util.M400cConstants
import com.vuzix.android.m400c.databinding.FragmentCameraDemoBinding
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class CameraFragment :
    BaseFragment<VideoUiState, VideoViewModel, FragmentCameraDemoBinding>(R.layout.fragment_camera_demo) {
    override val viewModel: VideoViewModel by viewModels()

    @Inject
    lateinit var usbManager: UsbManager

    @Inject
    lateinit var videoDevice: VuzixVideoDevice

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        videoDevice.usbDevice?.let { device ->
            Timber.d("$device")
            binding.tvVideoMessage.text = getString(R.string.video_device_available)
            usbManager.hasPermission(device).let {
                if (it) {
                    binding.tvVideoMessage.text =
                        getString(R.string.device_permission_granted, binding.tvVideoMessage.text)
                } else {
                    binding.tvVideoMessage.text = getString(
                        R.string.device_no_permission_granted,
                        binding.tvVideoMessage.text
                    )
                    val usbPermissionIntent = PendingIntent.getBroadcast(
                        requireContext(), 0, Intent(
                            M400cConstants.ACTION_USB_PERMISSION
                        ), 0
                    )
                    usbManager.requestPermission(device, usbPermissionIntent)
                }
            }
        }
    }

    fun initCamera() {
        val camera = UVCCameraProxy(requireContext())
        camera.config
            .isDebug(true)
            .setPicturePath(APPCACHE)
            .setDirName("camera")
            .setProductId(M400cConstants.VIDEO_PID)
            .setVendorId(M400cConstants.VIDEO_VID)
        camera.setPreviewTexture(binding.txvImage)
        camera.setConnectCallback(object : ConnectCallback {
            override fun onAttached(usbDevice: UsbDevice?) {
                camera.requestPermission(usbDevice)
            }

            override fun onGranted(usbDevice: UsbDevice?, granted: Boolean) {
                if (granted) {
                    camera.connectDevice(usbDevice)
                }
            }

            override fun onConnected(usbDevice: UsbDevice?) {
                camera.openCamera()
            }

            override fun onCameraOpened() {
                camera.setPreviewSize(640, 480)
                camera.startPreview()
            }

            override fun onDetached(usbDevice: UsbDevice?) {
                camera.closeCamera()
            }

        })
        camera.startPreview()
    }
}