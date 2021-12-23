package com.vuzix.android.m400c.audio.speakers

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.media.audiofx.Visualizer
import android.media.audiofx.Visualizer.OnDataCaptureListener
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.vuzix.android.m400c.R
import kotlin.math.abs
import kotlin.math.ceil

class SpeakerBarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    lateinit var visualizer: Visualizer
    private val paint = Paint().also { it.strokeCap = Paint.Cap.ROUND }

    private val density = 12f
    private val gap = 150

    var byteArray: ByteArray? = byteArrayOf()

    fun setupVisualizer(audioSessionId: Int) {
        visualizer = Visualizer(audioSessionId)
        visualizer.enabled = false
        visualizer.captureSize = Visualizer.getCaptureSizeRange()[1]
        visualizer.setDataCaptureListener(object : OnDataCaptureListener {
            override fun onWaveFormDataCapture(
                visualizer: Visualizer?,
                waveform: ByteArray?,
                samplingRate: Int
            ) {
                byteArray = waveform
                invalidate()
            }

            override fun onFftDataCapture(
                visualizer: Visualizer?,
                fft: ByteArray?,
                samplingRate: Int
            ) {
            }

        }, Visualizer.getMaxCaptureRate() / 2, true, false)
        visualizer.enabled = true
    }

    fun release() {
        visualizer.release()
        invalidate()
    }

    override fun onDraw(canvas: Canvas?) {
        byteArray?.let {
            if (it.isNotEmpty()) {
                val barWidth = width / density
                val div = it.size / density
                paint.strokeWidth = barWidth - gap

                var i = 0
                while (i < density) {
                    setBarColor(i)
                    val bytePosition = ceil((i * div).toDouble()).toInt()
                    val top: Int = (height / 2
                            + (128 - abs(it[bytePosition].toInt()))
                            * (height / 2) / 128)
                    val bottom: Int = (height / 2
                            - (128 - abs(it[bytePosition].toInt()))
                            * (height / 2) / 128)
                    val barX = i * barWidth + barWidth / 2
                    canvas?.drawLine(barX, bottom.toFloat() - 30, barX, (height / 2 - 30).toFloat(), paint)
                    canvas?.drawLine(barX, top.toFloat() + 30, barX, (height / 2 + 30).toFloat(), paint)
                    i++
                }
            }
        }
    }

    private fun setBarColor(pos: Int) {
        // We can do this because we know the number of bars is 12
        when(pos) {
            0 -> paint.color = ContextCompat.getColor(context, R.color.dark_moderate_violet)
            1 -> paint.color = ContextCompat.getColor(context, R.color.dark_pink)
            2 -> paint.color = ContextCompat.getColor(context, R.color.vivid_pink)
            3 -> paint.color = ContextCompat.getColor(context, R.color.strong_red)
            4 -> paint.color = ContextCompat.getColor(context, R.color.bright_red)
            5 -> paint.color = ContextCompat.getColor(context, R.color.vivid_orange)
            6 -> paint.color = ContextCompat.getColor(context, R.color.yellow)
            7 -> paint.color = ContextCompat.getColor(context, R.color.moderate_green)
            8 -> paint.color = ContextCompat.getColor(context, R.color.dark_lime_green)
            9 -> paint.color = ContextCompat.getColor(context, R.color.dark_green)
            10 -> paint.color = ContextCompat.getColor(context, R.color.dark_cyan)
            11 -> paint.color = ContextCompat.getColor(context, R.color.strong_blue)
        }
    }
}