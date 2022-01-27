package com.vuzix.android.m400c.audio.mic

import android.media.MediaRecorder
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnKeyListener
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.vuzix.android.m400c.R
import com.vuzix.android.m400c.databinding.FragmentMicrophoneDemoBinding
import com.vuzix.sdk.usbcviewer.M400cConstants
import kotlin.math.log10

class MicrophoneFragment : Fragment(), OnKeyListener {

    lateinit var binding: FragmentMicrophoneDemoBinding
    var recorder: MediaRecorder? = null

    var currentLocation = 50f

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_microphone_demo, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            setOutputFile("${requireActivity().externalCacheDir}/test.3gp")
        }
        recorder?.prepare()
        recorder?.start()

        val updater = Runnable {
            moveNeedle()
        }
        val handler = Handler(Looper.myLooper()!!)
        val runner = Thread {
            while (recorder != null) {
                Thread.sleep(250)
                handler.post(updater)
            }
        }
        runner.start()
    }


    override fun onStop() {
        recorder?.stop()
        recorder?.release()
        recorder = null
        requireActivity().externalCacheDir?.listFiles()?.let {
            if (it.isNotEmpty()) {
                it.forEach { file ->
                    file.delete()
                }
            }
        }
        super.onStop()
    }

    private fun getDecibels(): Double {
        recorder?.let {
            return 20 * log10(it.maxAmplitude.toDouble())
        } ?: return 0.0
    }

    private fun moveNeedle() {
        binding.ivNeedle?.apply {
            val dB = getDecibels()
            val convertedScale = (dB * .9).toFloat()
            val rotationAmount = convertedScale - currentLocation
            currentLocation = convertedScale
            pivotY = 475F
            if (!dB.isInfinite()) {
                rotation = rotationAmount
            }
        }
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

