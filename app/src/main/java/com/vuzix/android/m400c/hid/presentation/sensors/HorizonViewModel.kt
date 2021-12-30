package com.vuzix.android.m400c.hid.presentation.sensors

import androidx.lifecycle.viewModelScope
import com.vuzix.android.m400c.core.base.BaseViewModel
import com.vuzix.android.m400c.core.util.Failure
import com.vuzix.android.m400c.core.util.Failure.DataFailure
import com.vuzix.android.m400c.core.util.M400cConstants
import com.vuzix.android.m400c.core.util.SensorUtil
import com.vuzix.android.m400c.hid.data.source.HidSensorDataSource
import com.vuzix.android.m400c.hid.presentation.sensors.SensorAction.AccelUpdate
import com.vuzix.android.m400c.hid.presentation.sensors.SensorAction.Error
import com.vuzix.android.m400c.hid.presentation.sensors.SensorAction.GyroUpdate
import com.vuzix.android.m400c.hid.presentation.sensors.SensorAction.MagUpdate
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.launch

class HorizonViewModel constructor(
) : BaseViewModel<SensorUiState>(SensorUiState()) {
    override fun <F : Failure> onError(failure: F) {
        when (failure) {
            is DataFailure -> updateUiState { uiState ->
                uiState.copy(
                    action = Error(
                        failure.toString()
                    )
                )
            }
            else -> updateUiState { uiState -> uiState.copy(action = Error(failure.toString())) }
        }
    }

    init {
        /*viewModelScope.launch {
            hidDataSource.initConnection()
                .flatMapConcat {
                    hidDataSource.initSensor(M400cConstants.SENSOR_ACCELEROMETER_ID)
                }
                .flatMapConcat {
                    hidDataSource.initSensor(M400cConstants.SENSOR_GYRO_ID)
                }
                .flatMapConcat {
                    hidDataSource.initSensor(M400cConstants.SENSOR_MAGNETOMETER_ID)
                }
                .collect {
                    // Do nothing
                }
        }*/
    }

}