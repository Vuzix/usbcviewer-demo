package com.vuzix.android.m400c.hid.presentation.sensors

import com.vuzix.android.m400c.core.base.BaseUiState
import com.vuzix.android.m400c.hid.presentation.sensors.SensorAction.Default

data class SensorUiState(
    override val action: SensorAction = Default
) : BaseUiState
