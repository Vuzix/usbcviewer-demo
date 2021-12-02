package com.vuzix.android.m400c.audio.presentation

import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.vuzix.android.m400c.R
import com.vuzix.android.m400c.databinding.FragmentPlayAudioBinding
import java.io.File

class PlaySavedAudioFragment : Fragment() {

    lateinit var binding: FragmentPlayAudioBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_play_audio, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val filename = arguments?.getString("filename") ?: ""
        if (filename.isNotEmpty()) {
            binding.tvPlayAudioHeader.text = filename
            val file = Uri.fromFile(File(requireActivity().externalCacheDir, filename))
            val mediaPlayer = MediaPlayer.create(requireContext(), file)
            mediaPlayer.setOnCompletionListener {
                binding.imageButton.setImageResource(R.drawable.ic_baseline_play_arrow_24)
            }
            binding.imageButton.setOnClickListener {
                if (!mediaPlayer.isPlaying) {
                    mediaPlayer.start()
                    binding.imageButton.setImageResource(R.drawable.ic_baseline_stop_24)
                } else {
                    mediaPlayer.stop()
                    mediaPlayer.release()
                    binding.imageButton.setImageResource(R.drawable.ic_baseline_play_arrow_24)
                }
            }
        }
    }
}