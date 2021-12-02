package com.vuzix.android.m400c.video.presentation

import com.vuzix.android.m400c.core.base.BaseAction

sealed class VideoAction : BaseAction() {
    object Default : VideoAction()
    object Loading : VideoAction()
    data class Error(val errorMessage: String) : VideoAction()
}
