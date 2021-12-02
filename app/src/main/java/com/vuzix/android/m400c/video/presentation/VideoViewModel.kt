package com.vuzix.android.m400c.video.presentation

import androidx.lifecycle.viewModelScope
import com.vuzix.android.m400c.core.base.BaseViewModel
import com.vuzix.android.m400c.core.util.Failure
import com.vuzix.android.m400c.core.util.Failure.DataFailure
import com.vuzix.android.m400c.video.data.VideoInterfaceOneDataSource
import com.vuzix.android.m400c.video.data.VideoInterfaceTwoDataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VideoViewModel @Inject constructor(
    private val videoInterfaceOneDataSource: VideoInterfaceOneDataSource,
    private val videoInterfaceTwoDataSource: VideoInterfaceTwoDataSource
) : BaseViewModel<VideoUiState>(VideoUiState()) {
    override fun <F : Failure> onError(failure: F) {
        when (failure) {
            is DataFailure -> updateUiState { uiState ->
                uiState.copy(
                    action = VideoAction.Error(
                        failure.toString()
                    )
                )
            }
            else -> updateUiState { uiState -> uiState.copy(action = VideoAction.Error(failure.toString())) }
        }
    }

    init {
        viewModelScope.launch {
            initVideoInterfaceOne()
            initVideoInterfaceTwo()
        }
    }

    private suspend fun initVideoInterfaceOne() {
        videoInterfaceOneDataSource.dataFlow.collect { data ->
            data.handle(
                onSuccess = {},
                onFailure = { onError(it) }
            )
        }
    }

    private suspend fun initVideoInterfaceTwo() {
        videoInterfaceTwoDataSource.dataFlow.collect { data ->
            data.handle(
                onSuccess = {},
                onFailure = { onError(it) }
            )
        }
    }

    fun startVideoOneStream() = videoInterfaceOneDataSource.startStream()
    fun stopVideoOneStream() = videoInterfaceOneDataSource.stopStream()
    fun startVideoTwoStream() = videoInterfaceTwoDataSource.startStream()
    fun stopVideoTwoStream() = videoInterfaceTwoDataSource.stopStream()
}