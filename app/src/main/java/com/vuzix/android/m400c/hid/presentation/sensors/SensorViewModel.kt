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

class SensorViewModel constructor(
    private val hidDataSource: HidSensorDataSource,
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
        viewModelScope.launch {
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
        }
    }

//    private suspend fun initViewerControl() {
//        Timber.d("Init Viewer Control")
//        vcDataSource.dataFlow.collect { data ->
//            data.handle(
//                onSuccess = { updateViewerControlValue(it) },
//                onFailure = { onError(it) }
//            )
//        }
//    }

    fun startSensorStream() {
        hidDataSource.startStream()

        viewModelScope.launch {
            hidDataSource.dataFlow.collect { data ->
                data.handle(
                    onFailure = { onError(it) },
                    onSuccess = {
                        when (it[0].toInt()) {
                            M400cConstants.SENSOR_ACCELEROMETER_ID -> updateHidAccelSensorValue(it)
                            M400cConstants.SENSOR_GYRO_ID -> updateHidGyroSensorValue(it)
                            M400cConstants.SENSOR_MAGNETOMETER_ID -> updateHidMagSensorValue(it)
                        }
                    }
                )
            }
        }
    }

    fun stopSensorStream() = hidDataSource.stopStream()
//    fun startVcStream() = vcDataSource.startStream()
//    fun stopVcStream() = vcDataSource.stopStream()

    private fun updateHidGyroSensorValue(value: ByteArray) {
        updateUiState { uiState ->
            uiState.copy(
                action = GyroUpdate(
                    SensorUtil.createGyroObject(
                        value
                    )
                )
            )
        }
    }

    private fun updateHidAccelSensorValue(value: ByteArray) {
        updateUiState { uiState ->
            uiState.copy(
                action = AccelUpdate(
                    SensorUtil.createAccelData(
                        value
                    )
                )
            )
        }
    }

    private fun updateHidMagSensorValue(value: ByteArray) {
        updateUiState { uiState ->
            uiState.copy(
                action = MagUpdate(
                    SensorUtil.createMagData(
                        value
                    )
                )
            )
        }
    }

//    private fun updateViewerControlValue(value: ByteArray) {
//        updateUiState { uiState -> uiState.copy(action = ViewerControlUpdate(value.strPrint())) }
//    }

}