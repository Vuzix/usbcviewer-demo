package com.vuzix.android.m400c.audio.speakers

import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.vuzix.android.m400c.R
import com.vuzix.android.m400c.databinding.FragmentSpeakerDemoBinding

class SpeakerFragment : Fragment() {

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

}