package com.vuzix.android.m400c.hid.presentation.buttons

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnKeyListener
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.vuzix.android.m400c.R
import com.vuzix.android.m400c.core.util.M400cConstants
import com.vuzix.android.m400c.databinding.FragmentButtonDemoBinding
import timber.log.Timber

class ButtonDemoFragment : Fragment(), OnKeyListener {

    lateinit var binding: FragmentButtonDemoBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_button_demo, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.setOnKeyListener(this)
    }

    override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
        if (event?.keyCode == KeyEvent.KEYCODE_BACK) {
            requireActivity().onBackPressed()
        } else {
            when (event?.scanCode) {
                28 -> setButtonPressedText("Key One", "Enter")
                105 -> setButtonPressedText("Key Two", "Move Left")
                106 -> setButtonPressedText("Key Three", "Move Right")
                186 -> setButtonPressedText("Key Four", "Unknown")
                111 -> setButtonPressedText("Key One Long Press", "Escape")
                108 -> setButtonPressedText("Key Two Long Press", "Move Down")
                103 -> setButtonPressedText("Key Three Long Press", "Move Up")
            }
            return true
        }
        return false
    }

    private fun setButtonPressedText(which: String, command: String) {
        binding.tvButtonsWhich?.text = which
        binding.tvButtonsCommand?.text = command
    }






}