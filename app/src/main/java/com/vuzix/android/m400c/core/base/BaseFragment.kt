package com.vuzix.android.m400c.core.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer

abstract class BaseFragment<
        UiStateType : BaseUiState,
        ViewModelType : BaseViewModel<UiStateType>,
        BindingType : ViewDataBinding>(@LayoutRes val layoutId: Int) : Fragment() {

    lateinit var binding: BindingType

    abstract val viewModel: ViewModelType
    val uiState: UiStateType get() = viewModel.uiState

    final override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, layoutId, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        bindUiStateLiveData()
    }

    private fun bindUiStateLiveData() {
        viewModel.uiStateLiveData.observe {
            uiState as? UiStateType ?: throw IllegalStateException("Wrong UiStateType")
            onUiStateUpdated(uiState)
        }
    }

    open fun onUiStateUpdated(uiState: UiStateType) {}

    fun <T> LiveData<T>.observe(onChange: (T) -> Unit) {
        observe(viewLifecycleOwner, Observer<T> { t -> onChange(t) })
    }
}