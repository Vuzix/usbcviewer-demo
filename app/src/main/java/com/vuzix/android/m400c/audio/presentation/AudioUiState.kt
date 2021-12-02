package com.vuzix.android.m400c.audio.presentation

import com.vuzix.android.m400c.core.base.BaseUiState

data class AudioUiState(
    override val action: AudioAction = AudioAction.Default
) : BaseUiState