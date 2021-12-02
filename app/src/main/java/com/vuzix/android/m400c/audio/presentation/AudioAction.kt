package com.vuzix.android.m400c.audio.presentation

import com.vuzix.android.m400c.core.base.BaseAction

sealed class AudioAction : BaseAction() {
    object Default : AudioAction()
    data class Error(val errorMessage: String) : AudioAction()
    data class DataReceived(val data: String) : AudioAction()
}