package com.vuzix.android.m400c.hid.presentation

import com.vuzix.android.m400c.core.base.BaseAction
import com.vuzix.android.m400c.hid.domain.AccelData
import com.vuzix.android.m400c.hid.domain.GyroData
import com.vuzix.android.m400c.hid.domain.MagData

sealed class HidAction : BaseAction() {
    object Default : HidAction()
    object Loading : HidAction()
    data class Error(val errorMessage: String) : HidAction()
    data class KeyboardPress(val key: String) : HidAction()
    data class GyroUpdate(val gyroData: GyroData) : HidAction()
    data class AccelUpdate(val accelData: AccelData) : HidAction()
    data class MagUpdate(val magData: MagData) : HidAction()
    data class MessageUpdate(val message: String) : HidAction()
    data class ViewerControlUpdate(val value: String) : HidAction()
}