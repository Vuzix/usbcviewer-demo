package com.vuzix.android.m400c.audio.presentation

import android.app.PendingIntent
import android.content.Intent
import android.hardware.usb.UsbManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.vuzix.android.m400c.R
import com.vuzix.android.m400c.audio.presentation.AudioAction.DataReceived
import com.vuzix.android.m400c.audio.presentation.AudioAction.Default
import com.vuzix.android.m400c.audio.presentation.AudioAction.Error
import com.vuzix.android.m400c.audio.presentation.adapter.AudioFileAdapter
import com.vuzix.android.m400c.common.domain.entity.VuzixAudioDevice
import com.vuzix.android.m400c.core.base.BaseFragment
import com.vuzix.android.m400c.core.util.M400cConstants
import com.vuzix.android.m400c.databinding.FragmentAudioBinding
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class AudioFragment :
    BaseFragment<AudioUiState, AudioViewModel, FragmentAudioBinding>(R.layout.fragment_audio) {

    override val viewModel: AudioViewModel by viewModels()

    @Inject
    lateinit var usbManager: UsbManager

    @Inject
    lateinit var audioDevice: VuzixAudioDevice

    private var fileName: String = ""
    lateinit var audioAdapter: AudioFileAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Timber.d("${audioDevice.usbDevice}")
        fileName = "${requireActivity().externalCacheDir?.absolutePath}/audiorecordtest"
        audioDevice.usbDevice?.let { device ->
            binding.tvAudioMessage.text = getString(R.string.audio_device_available)
            usbManager.hasPermission(device).let {
                if (it) {
                    binding.tvAudioMessage.text =
                        getString(R.string.device_permission_granted, binding.tvAudioMessage.text)
                } else {
                    binding.tvAudioMessage.text = getString(
                        R.string.device_no_permission_granted,
                        binding.tvAudioMessage.text
                    )
                    val usbPermissionIntent = PendingIntent.getBroadcast(
                        requireContext(),
                        0,
                        Intent(M400cConstants.ACTION_USB_PERMISSION),
                        0
                    )
                    usbManager.requestPermission(device, usbPermissionIntent)
                }
            }
        }

        val fileList = requireActivity().externalCacheDir?.listFiles()?.toList() ?: emptyList()
        audioAdapter = AudioFileAdapter(fileList.map { it.name })
        binding.rvAudioAvailableFiles.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = audioAdapter
        }

        val mediaPlayer = MediaPlayer.create(requireContext(), R.raw.rickroll)
        val recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)

//            try {
//                prepare()
//            } catch (e: IOException) {
//                Timber.e(e)
//            }
        }

        binding.btnAudioSound.apply {
            setOnClickListener {
                if (this.text == getString(R.string.button_audio_sound_off)) {
                    this.text = getString(R.string.button_audio_sound_on)
                    mediaPlayer.start()
                } else {
                    this.text = getString(R.string.button_audio_sound_off)
                    mediaPlayer.stop()
                    mediaPlayer.reset()
                }
            }
        }

        binding.btnAudioRecord?.apply {
            setOnClickListener {
                if (this.text == getString(R.string.button_audio_record_off)) {
                    this.text = getString(R.string.button_audio_record_on)
                    Timber.d(fileName)
                    recorder.setOutputFile("${fileName}${System.currentTimeMillis()}.3gp")
                    recorder.prepare()
                    recorder.start()
                } else {
                    this.text = getString(R.string.button_audio_record_off)
                    recorder.stop()
                    recorder.release()
                    val updatedList = requireActivity().externalCacheDir?.listFiles()?.map { it.name } ?: emptyList()
                    audioAdapter.updateList(updatedList)
                }
            }
        }

        binding.btnAudioClearCache?.apply {
            setOnClickListener {
                if (fileList.isNotEmpty()) {
                    fileList.forEach { it.delete() }
                    audioAdapter.updateList(emptyList())
                }
            }
        }
    }

    override fun onUiStateUpdated(uiState: AudioUiState) {
        when (uiState.action) {
            is Default -> {
            }
            is Error -> binding.tvAudioMessage.text = uiState.action.errorMessage
            is DataReceived -> binding.tvAudioMessage.text = uiState.action.data
        }
    }
}