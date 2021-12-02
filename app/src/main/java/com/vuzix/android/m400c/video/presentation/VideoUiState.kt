package com.vuzix.android.m400c.video.presentation

import com.vuzix.android.m400c.core.base.BaseAction
import com.vuzix.android.m400c.core.base.BaseUiState

data class VideoUiState(
    override val action: VideoAction = VideoAction.Default
) : BaseUiState