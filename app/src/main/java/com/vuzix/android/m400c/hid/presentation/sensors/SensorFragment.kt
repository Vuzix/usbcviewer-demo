package com.vuzix.android.m400c.hid.presentation.sensors

import android.content.Context
import android.hardware.usb.UsbManager
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.vuzix.android.m400c.R
import com.vuzix.android.m400c.common.domain.entity.VuzixHidDevice
import com.vuzix.android.m400c.core.base.BaseFragment
import com.vuzix.android.m400c.core.util.DeviceUtil
import com.vuzix.android.m400c.core.util.M400cConstants
import com.vuzix.android.m400c.databinding.FragmentSensorDemoBinding
import com.vuzix.android.m400c.hid.data.model.HidSensorInterface
import com.vuzix.android.m400c.hid.data.source.HidSensorDataSource
import com.vuzix.android.m400c.hid.presentation.sensors.SensorAction.AccelUpdate
import com.vuzix.android.m400c.hid.presentation.sensors.SensorAction.Default
import com.vuzix.android.m400c.hid.presentation.sensors.SensorAction.Error
import com.vuzix.android.m400c.hid.presentation.sensors.SensorAction.GyroUpdate
import com.vuzix.android.m400c.hid.presentation.sensors.SensorAction.Loading
import com.vuzix.android.m400c.hid.presentation.sensors.SensorAction.MagUpdate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class SensorFragment :
    BaseFragment<SensorUiState, SensorViewModel, FragmentSensorDemoBinding>(R.layout.fragment_sensor_demo) {
    override val viewModel: SensorViewModel by viewModels() {
        SensorViewModelFactory(hidSensorDataSource)
    }

    lateinit var usbManager: UsbManager
    lateinit var hidDevice: VuzixHidDevice
    lateinit var hidSensorDataSource: HidSensorDataSource

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        usbManager = requireContext().getSystemService(Context.USB_SERVICE) as UsbManager
        hidDevice = DeviceUtil.getHidDevice(usbManager)
        val hidSensorInterface = hidDevice.usbDevice.let {
            val intf = it!!.getInterface(M400cConstants.HID_SENSOR)
            val inboundEndpoint = intf.getEndpoint(M400cConstants.HID_SENSOR_INBOUND)
            HidSensorInterface(intf, inboundEndpoint)
        }
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
        hidSensorDataSource = HidSensorDataSource(scope, usbManager, hidDevice, hidSensorInterface)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.startSensorStream()
    }

    override fun onStop() {
        viewModel.stopSensorStream()
        super.onStop()
    }

    override fun onUiStateUpdated(uiState: SensorUiState) {
        when (uiState.action) {
            is Default -> {
                // Do nothing
            }
            is Error -> binding.tvHidMessage?.text = uiState.action.errorMessage
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
        }
    }
}