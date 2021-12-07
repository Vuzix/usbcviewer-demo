package com.vuzix.android.m400c.audio.presentation.mic

import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.vuzix.android.m400c.R
import com.vuzix.android.m400c.audio.domain.AudioFileInfo
import com.vuzix.android.m400c.audio.presentation.adapter.AudioFileAdapter
import com.vuzix.android.m400c.common.presentation.ItemClickListener
import com.vuzix.android.m400c.databinding.FragmentMicrophoneDemoBinding
import timber.log.Timber
import java.io.File
import java.io.IOException

class MicrophoneFragment : Fragment() {

    lateinit var binding: FragmentMicrophoneDemoBinding
    lateinit var audioAdapter: AudioFileAdapter
    private var fileName: String = ""
    private var mediaPlayer: MediaPlayer? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_microphone_demo, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
        }

        audioAdapter = AudioFileAdapter(requireActivity().externalCacheDir?.list()?.toMutableList()?.map { it -> AudioFileInfo(it) } ?: emptyList(), object : ItemClickListener {
            override fun onItemClicked(name: String) {
                fileName = name
                binding.btnMicrophonePlay?.isEnabled = true
                binding.tvSelectedFile?.text = name
            }
        })

        binding.rvMicrophoneAvailableFiles?.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = audioAdapter
        }

        binding.btnMicrophoneRecord?.apply {
            setOnClickListener {
                if (this.text == getString(R.string.record)) {
                    this.text = getString(R.string.stop)
                    recorder.prepare()
                    recorder.start()
                } else {
                    this.text = getString(R.string.record)
                    recorder.stop()
                    recorder.release()
                    audioAdapter.updateList(requireActivity().externalCacheDir?.list()?.toList()?.map { it -> AudioFileInfo(it) } ?: emptyList())
                }
            }
        }

        binding.btnMicrophonePlay?.apply {
            setOnClickListener {
                if (this.text == getString(R.string.play)) {
                    this.text = getString(R.string.stop)
                    startPlay()
                } else {
                    this.text = getString(R.string.play)
                    stopPlay()
                }
            }
        }
    }

    private fun startPlay() {
        mediaPlayer = MediaPlayer.create(requireContext(), Uri.fromFile(File(requireActivity().externalCacheDir, fileName)))
        mediaPlayer?.start()
    }

    private fun stopPlay() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    override fun onStop() {
        super.onStop()
        stopPlay()
    }
}