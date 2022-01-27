package com.vuzix.android.m400c.hid.buttons

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnKeyListener
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.vuzix.android.m400c.R
import com.vuzix.android.m400c.databinding.FragmentButtonDemoBinding
import com.vuzix.sdk.usbcviewer.M400cConstants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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
        when {
            event?.keyCode == KeyEvent.KEYCODE_BACK -> {
                requireActivity().onBackPressed()
            }
            else -> {
                when (event?.scanCode) {
                    M400cConstants.KEY_FRONT -> setButtonPressedImage(binding.ivButtonFront, R.drawable.button_front)
                    M400cConstants.KEY_MIDDLE -> setButtonPressedImage(binding.ivButtonMiddle, R.drawable.button_middle)
                    M400cConstants.KEY_BACK -> setButtonPressedImage(binding.ivButtonBack, R.drawable.button_back)
                    M400cConstants.KEY_BACK_LONG, M400cConstants.KEY_FRONT_LONG, M400cConstants.KEY_MIDDLE_LONG -> requireActivity().onBackPressed()
                }
                return true
            }
        }
        return false
    }

    private fun setButtonPressedImage(imageView: ImageView?, @DrawableRes id: Int) {
        imageView?.visibility = View.VISIBLE
        imageView?.setImageDrawable(ContextCompat.getDrawable(requireContext(), id))
        GlobalScope.launch {
            delay(2500)
            launch(Dispatchers.Main) {
                imageView?.visibility = View.INVISIBLE
            }
        }
    }


}