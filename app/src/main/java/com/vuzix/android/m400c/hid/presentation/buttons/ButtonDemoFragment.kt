package com.vuzix.android.m400c.hid.presentation.buttons

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnKeyListener
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.vuzix.android.m400c.R
import com.vuzix.android.m400c.core.util.M400cConstants
import com.vuzix.android.m400c.databinding.FragmentButtonDemoBinding
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
        if (event?.keyCode == KeyEvent.KEYCODE_BACK) {
            requireActivity().onBackPressed()
        } else if (event?.action == KeyEvent.ACTION_UP) {
            GlobalScope.launch {
                delay(2500)
                launch(Dispatchers.Main) {
                    binding.ivButtons?.visibility = View.INVISIBLE
                }
            }
        } else {
            when (event?.scanCode) {
                M400cConstants.KEY_TWO -> setButtonPressedImage(R.drawable.buttons_3)
                M400cConstants.KEY_THREE -> setButtonPressedImage(R.drawable.buttons_2)
                M400cConstants.KEY_FOUR -> setButtonPressedImage(R.drawable.buttons_1)
                M400cConstants.KEY_ONE_LONG, M400cConstants.KEY_TWO_LONG, M400cConstants.KEY_THREE_LONG -> requireActivity().onBackPressed()
            }
            return true
        }
        return false
    }

    private fun setButtonPressedImage(@DrawableRes id: Int) {
        binding.ivButtons?.visibility = View.VISIBLE
        binding.ivButtons?.setImageDrawable(ContextCompat.getDrawable(requireContext(), id))
    }


}