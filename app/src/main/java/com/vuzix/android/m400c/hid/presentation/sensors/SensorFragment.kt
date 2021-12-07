package com.vuzix.android.m400c.hid.presentation.sensors

import android.hardware.usb.UsbManager
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.vuzix.android.m400c.R
import com.vuzix.android.m400c.common.domain.entity.VuzixHidDevice
import com.vuzix.android.m400c.core.base.BaseFragment
import com.vuzix.android.m400c.databinding.FragmentSensorDemoBinding
import com.vuzix.android.m400c.hid.presentation.sensors.SensorAction.AccelUpdate
import com.vuzix.android.m400c.hid.presentation.sensors.SensorAction.Default
import com.vuzix.android.m400c.hid.presentation.sensors.SensorAction.Error
import com.vuzix.android.m400c.hid.presentation.sensors.SensorAction.GyroUpdate
import com.vuzix.android.m400c.hid.presentation.sensors.SensorAction.Loading
import com.vuzix.android.m400c.hid.presentation.sensors.SensorAction.MagUpdate
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SensorFragment :
    BaseFragment<SensorUiState, SensorViewModel, FragmentSensorDemoBinding>(R.layout.fragment_sensor_demo) {
    override val viewModel: SensorViewModel by viewModels()

    @Inject
    lateinit var usbManager: UsbManager
    @Inject
    lateinit var hidDevice: VuzixHidDevice

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnHidSensor.apply {
            setOnClickListener {
                if (this.text == getString(R.string.start)) {
                    this.text = getString(R.string.stop)
                    viewModel.startSensorStream()
                } else {
                    this.text = getString(R.string.start)
                    viewModel.stopSensorStream()
                }
            }
        }
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