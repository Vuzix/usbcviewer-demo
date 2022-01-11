package com.vuzix.android.m400c.audio.speakers

import android.media.MediaPlayer
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnKeyListener
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.vuzix.android.m400c.R
import com.vuzix.m400cconnectivitysdk.M400cConstants
import com.vuzix.android.m400c.databinding.FragmentSpeakerDemoBinding

class SpeakerFragment : Fragment(), OnKeyListener {

    lateinit var binding: FragmentSpeakerDemoBinding
    lateinit var mediaPlayer: MediaPlayer

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_speaker_demo, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mediaPlayer = MediaPlayer.create(requireContext(), R.raw.m400c_demo_sound)
        binding.speakerVis?.setupVisualizer(mediaPlayer.audioSessionId)
        mediaPlayer.setOnCompletionListener {
            requireActivity().onBackPressed()
        }
        mediaPlayer.start()
    }

    override fun onPause() {
        mediaPlayer.stop()
        super.onPause()
    }

    override fun onStop() {
        mediaPlayer.release()
        binding.speakerVis?.release()
        super.onStop()
    }

    override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
        if (event?.keyCode == KeyEvent.KEYCODE_BACK) {
            requireActivity().onBackPressed()
        } else {
            when (event?.scanCode) {
                M400cConstants.KEY_BACK_LONG,
                M400cConstants.KEY_FRONT_LONG,
                M400cConstants.KEY_MIDDLE_LONG ->
                    if (event.action != KeyEvent.ACTION_UP) {
                        requireActivity().onBackPressed()
                    }
            }
            return true
        }
        return false
    }

}