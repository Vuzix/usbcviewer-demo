package com.vuzix.android.m400c.hid.presentation

import android.app.PendingIntent
import android.content.Intent
import android.hardware.usb.UsbManager
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.vuzix.android.m400c.R
import com.vuzix.android.m400c.common.domain.entity.VuzixHidDevice
import com.vuzix.android.m400c.core.base.BaseFragment
import com.vuzix.android.m400c.core.util.M400cConstants
import com.vuzix.android.m400c.databinding.FragmentHidBinding
import com.vuzix.android.m400c.hid.presentation.HidAction.AccelUpdate
import com.vuzix.android.m400c.hid.presentation.HidAction.Default
import com.vuzix.android.m400c.hid.presentation.HidAction.Error
import com.vuzix.android.m400c.hid.presentation.HidAction.KeyboardPress
import com.vuzix.android.m400c.hid.presentation.HidAction.Loading
import com.vuzix.android.m400c.hid.presentation.HidAction.GyroUpdate
import com.vuzix.android.m400c.hid.presentation.HidAction.MagUpdate
import com.vuzix.android.m400c.hid.presentation.HidAction.MessageUpdate
import com.vuzix.android.m400c.hid.presentation.HidAction.ViewerControlUpdate
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HidFragment :
    BaseFragment<HidUiState, HidViewModel, FragmentHidBinding>(R.layout.fragment_hid) {
    override val viewModel: HidViewModel by viewModels()

    @Inject
    lateinit var usbManager: UsbManager
    @Inject
    lateinit var hidDevice: VuzixHidDevice

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hidDevice.usbDevice?.let { device ->
            binding.tvHidMessage.text = getString(R.string.hid_device_available)
            usbManager.hasPermission(device).let {
                if (it) {
                    binding.tvHidMessage.text = getString(R.string.device_permission_granted, binding.tvHidMessage.text)
                } else {
                    binding.tvHidMessage.text = getString(R.string.device_no_permission_granted, binding.tvHidMessage.text)
                    val usbPermissionIntent = PendingIntent.getBroadcast(
                        requireContext(),
                        0,
                        Intent(M400cConstants.ACTION_USB_PERMISSION),
                        PendingIntent.FLAG_IMMUTABLE
                    )
                    usbManager.requestPermission(device, usbPermissionIntent)
                }
            }
        }

        binding.btnKeyboard.apply {
            setOnClickListener {
                if (this.text == getString(R.string.button_hid_keyboard_off)) {
                    this.text = getString(R.string.button_hid_keyboard_on)
                    viewModel.startKeyboardStream()
                } else {
                    this.text = getString(R.string.button_hid_keyboard_off)
                    viewModel.stopKeyboardStream()
                }
            }
        }

        binding.btnHidSensor.apply {
            setOnClickListener {
                if (this.text == getString(R.string.button_hid_sensor_off)) {
                    this.text = getString(R.string.button_hid_sensor_on)
                    viewModel.startSensorStream()
//                    viewModel.startAccelHidStream()
                } else {
                    this.text = getString(R.string.button_hid_sensor_off)
                    viewModel.stopSensorStream()
//                    viewModel.stopAccelHidStream()
                }
            }

            binding.btnViewerControl.apply {
                setOnClickListener {
                    if (this.text == getString(R.string.button_hid_control_off)) {
                        this.text = getString(R.string.button_hid_control_on)
                        viewModel.startVcStream()
                    } else {
                        this.text = getString(R.string.button_hid_control_off)
                        viewModel.stopVcStream()
                    }
                }
            }
        }
    }

    override fun onUiStateUpdated(uiState: HidUiState) {
        when (uiState.action) {
            is Default -> {
                // Do nothing
            }
            is Error -> binding.tvHidMessage.text = uiState.action.errorMessage
            is KeyboardPress -> {
                binding.tvKeyboardText?.text = uiState.action.key
            }
            is Loading -> {
                // Do nothing for now (may not need this)
            }
            is GyroUpdate -> {
                uiState.action.gyroData.let {
                    binding.tvHidGyroX.text = getString(R.string.hid_sensor_x, it.gyroX.toInt())
                    binding.tvHidGyroY.text = getString(R.string.hid_sensor_y, it.gyroY.toInt())
                    binding.tvHidGyroZ.text = getString(R.string.hid_sensor_z, it.gyroZ.toInt())
                }
            }
            is AccelUpdate -> {
                uiState.action.accelData.let {
                    binding.tvHidAccelX.text = getString(R.string.hid_accel_x, it.accelX)
                    binding.tvHidAccelY.text = getString(R.string.hid_accel_y, it.accelY)
                    binding.tvHidAccelZ.text = getString(R.string.hid_accel_z, it.accelZ)
                }
            }
            is MagUpdate -> {
                uiState.action.magData.let {
                    binding.tvHidMagX.text = getString(R.string.hid_sensor_x, it.magX.toInt())
                    binding.tvHidMagY.text = getString(R.string.hid_sensor_y, it.magY.toInt())
                    binding.tvHidMagZ.text = getString(R.string.hid_sensor_z, it.magZ.toInt())
                }
            }
            is MessageUpdate ->  binding.tvHidMessage.text = uiState.action.message
            is ViewerControlUpdate -> binding.tvHidMessage.text = uiState.action.value
        }
    }
}