package com.vuzix.android.m400c.audio.presentation.speakers

import android.hardware.usb.UsbManager
import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.vuzix.android.m400c.R
import com.vuzix.android.m400c.audio.presentation.adapter.AudioFileAdapter
import com.vuzix.android.m400c.common.domain.entity.VuzixAudioDevice
import com.vuzix.android.m400c.databinding.FragmentAudioBinding
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class SpeakerFragment : Fragment() {

    @Inject
    lateinit var usbManager: UsbManager

    @Inject
    lateinit var audioDevice: VuzixAudioDevice

    lateinit var binding: FragmentAudioBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_audio, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Timber.d("${audioDevice.usbDevice}")

        val mediaPlayer = MediaPlayer.create(requireContext(), R.raw.rickroll)
//
        binding.btnSpeakersPlay?.apply {
            this.setOnClickListener {
                if (!mediaPlayer.isPlaying) {
                    this.text = getString(R.string.stop)
                    mediaPlayer.start()
                } else {
                    this.text = getString(R.string.play)
                    mediaPlayer.stop()
                }
            }
        }
    }
}