package com.vuzix.android.m400c.hid.presentation.sensors

import com.vuzix.android.m400c.core.base.BaseAction
import com.vuzix.android.m400c.hid.domain.AccelData
import com.vuzix.android.m400c.hid.domain.GyroData
import com.vuzix.android.m400c.hid.domain.MagData

sealed class SensorAction : BaseAction() {
    object Default : SensorAction()
    object Loading : SensorAction()
    data class Error(val errorMessage: String) : SensorAction()
    data class GyroUpdate(val gyroData: GyroData) : SensorAction()
    data class AccelUpdate(val accelData: AccelData) : SensorAction()
    data class MagUpdate(val magData: MagData) : SensorAction()
}