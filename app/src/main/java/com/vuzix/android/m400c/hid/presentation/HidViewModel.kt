package com.vuzix.android.m400c.hid.presentation

import androidx.lifecycle.viewModelScope
import com.vuzix.android.m400c.core.base.BaseViewModel
import com.vuzix.android.m400c.core.util.Failure
import com.vuzix.android.m400c.core.util.Failure.DataFailure
import com.vuzix.android.m400c.core.util.SensorUtil
import com.vuzix.android.m400c.core.util.M400cConstants
import com.vuzix.android.m400c.core.util.strPrint
import com.vuzix.android.m400c.hid.data.source.HidSensorDataSource
import com.vuzix.android.m400c.hid.data.source.ViewerControlDataSource
import com.vuzix.android.m400c.hid.data.source.ViewerKeyboardDataSource
import com.vuzix.android.m400c.hid.presentation.HidAction.AccelUpdate
import com.vuzix.android.m400c.hid.presentation.HidAction.KeyboardPress
import com.vuzix.android.m400c.hid.presentation.HidAction.GyroUpdate
import com.vuzix.android.m400c.hid.presentation.HidAction.MagUpdate
import com.vuzix.android.m400c.hid.presentation.HidAction.MessageUpdate
import com.vuzix.android.m400c.hid.presentation.HidAction.ViewerControlUpdate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class HidViewModel @Inject constructor(
    private val kbDataSource: ViewerKeyboardDataSource,
    private val hidDataSource: HidSensorDataSource,
    private val vcDataSource: ViewerControlDataSource
) : BaseViewModel<HidUiState>(HidUiState()) {
    override fun <F : Failure> onError(failure: F) {
        when (failure) {
            is DataFailure -> updateUiState { uiState ->
                uiState.copy(
                    action = HidAction.Error(
                        failure.toString()
                    )
                )
            }
            else -> updateUiState { uiState -> uiState.copy(action = HidAction.Error(failure.toString())) }
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
                    updateUiState { uiState -> uiState.copy(action = MessageUpdate("All Sensors Initialized")) }
                }
//            kbDataSource.initConnection()
//                .collect {
//                    val message = if (it) {
//                        "Keyboard initialized"
//                    } else {
//                        "Keyboard NOT initialized"
//                    }
//                    updateUiState { uiState -> uiState.copy(action = MessageUpdate(message)) }
//                }
        }
    }

    private suspend fun initViewerControl() {
        Timber.d("Init Viewer Control")
        vcDataSource.dataFlow.collect { data ->
            data.handle(
                onSuccess = { updateViewerControlValue(it) },
                onFailure = { onError(it) }
            )
        }
    }

    fun startKeyboardStream() {
        Timber.d("Init keyboard")
        kbDataSource.startStream()
        viewModelScope.launch {
            kbDataSource.dataFlow.collect { data ->
                data.handle(
                    onSuccess = {
                        updateKeyPressValue(it)
                    },
                    onFailure = {
                        onError(it)
                    }
                )
            }
        }
    }

    fun stopKeyboardStream() = kbDataSource.stopStream()
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
    fun startVcStream() = vcDataSource.startStream()
    fun stopVcStream() = vcDataSource.stopStream()

    private fun updateKeyPressValue(value: ByteArray) {
        Timber.d(value.strPrint())
        val key = when (value.strPrint()) {
            M400cConstants.KEY_ONE -> "Key One"
            M400cConstants.KEY_ONE_LONG -> "Key One Long Press"
            M400cConstants.KEY_TWO -> "Key Two"
            M400cConstants.KEY_TWO_LONG -> "Key Two Long Press"
            M400cConstants.KEY_THREE -> "Key Three"
            M400cConstants.KEY_THREE_LONG -> "Key Three Long Press"
            M400cConstants.KEY_FOUR -> "Key Four"
            else -> ""
        }
        if (key.isNotEmpty()) {
            updateUiState { uiState -> uiState.copy(action = KeyboardPress(key)) }
        }
    }

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

    private fun updateViewerControlValue(value: ByteArray) {
        updateUiState { uiState -> uiState.copy(action = ViewerControlUpdate(value.strPrint())) }
    }

}