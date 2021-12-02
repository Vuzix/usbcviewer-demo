package com.vuzix.android.m400c.core.base

import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vuzix.android.m400c.core.util.Failure
import kotlinx.coroutines.launch

abstract class BaseViewModel<UiStateType : BaseUiState>(uiState: UiStateType) : ViewModel() {

    private val _uiStateLiveData: MutableLiveData<UiStateType> = MutableLiveData()
    val uiStateLiveData: LiveData<UiStateType> = _uiStateLiveData
    val uiState: UiStateType
        get() = uiStateLiveData.value ?: throw IllegalStateException("BaseUiState is null")

    init {
        _uiStateLiveData.value = uiState
    }

    open fun onArgumentsReceived(arguments: Bundle) {}

    fun updateUiState(update: (UiStateType) -> UiStateType) {
        uiStateLiveData.value?.let {
            val newState = update(it)
            check(!(newState === uiStateLiveData.value)) { "BaseUiState is the same object. Use .copy" }
            _uiStateLiveData.value = newState
        } ?: throw IllegalStateException("BaseUiState is null")
    }

    abstract fun <F : Failure> onError(failure: F)
}