package com.vuzix.android.m400c.hid.presentation.sensors

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.vuzix.android.m400c.hid.data.source.HidSensorDataSource

class SensorViewModelFactory (private val hidSensorDataSource: HidSensorDataSource): ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SensorViewModel(hidSensorDataSource) as T
    }
}