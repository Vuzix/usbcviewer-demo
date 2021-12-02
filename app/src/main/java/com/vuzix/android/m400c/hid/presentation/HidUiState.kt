package com.vuzix.android.m400c.hid.presentation

import com.vuzix.android.m400c.core.base.BaseUiState

data class HidUiState(
    override val action: HidAction = HidAction.Default
) : BaseUiState
