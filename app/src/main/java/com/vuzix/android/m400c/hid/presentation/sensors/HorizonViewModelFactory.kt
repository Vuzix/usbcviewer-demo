package com.vuzix.android.m400c.hid.presentation.sensors

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.vuzix.android.m400c.hid.data.source.HidSensorDataSource

class HorizonViewModelFactory (): ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HorizonViewModel() as T
    }
}