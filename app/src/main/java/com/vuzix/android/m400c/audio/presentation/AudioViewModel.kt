package com.vuzix.android.m400c.audio.presentation

import com.vuzix.android.m400c.audio.data.InboundAudioDataSource
import com.vuzix.android.m400c.audio.presentation.AudioAction.DataReceived
import com.vuzix.android.m400c.core.base.BaseViewModel
import com.vuzix.android.m400c.core.util.Failure
import com.vuzix.android.m400c.core.util.Failure.DataFailure
import com.vuzix.android.m400c.core.util.strPrint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

@HiltViewModel
class AudioViewModel @Inject constructor(private val inboundAudioDataSource: InboundAudioDataSource) : BaseViewModel<AudioUiState>(
    AudioUiState()) {

    override fun <F : Failure> onError(failure: F) {
        when (failure) {
            is DataFailure -> {
                updateUiState { uiState -> uiState.copy(action = AudioAction.Error(failure.toString())) }
            }
            else -> updateUiState { uiState -> uiState.copy(action = AudioAction.Error(failure.toString())) }
        }
    }

    private suspend fun initInboundAudio() {
        inboundAudioDataSource.dataFlow.collect { data ->
            data.handle(
                onSuccess = {
                    updateAudioStreamValue(it)
                },
                onFailure = {
                    onError(it)
                }
            )
        }
    }

    fun startAudioStream() = inboundAudioDataSource.startStream()
    fun stopAudioStream() = inboundAudioDataSource.stopStream()

    private fun updateAudioStreamValue(value: ByteArray) {
        updateUiState { uiState -> uiState.copy(action = DataReceived(value.strPrint())) }
    }

}