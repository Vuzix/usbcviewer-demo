package com.vuzix.android.m400c.hid.sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.hardware.SensorManager.DynamicSensorCallback
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.vuzix.android.m400c.R
import com.vuzix.android.m400c.databinding.FragmentSensorDemoBinding
import com.vuzix.sdk.usbcviewer.sensors.Sensors
import com.vuzix.sdk.usbcviewer.sensors.VuzixSensorEvent
import com.vuzix.sdk.usbcviewer.sensors.VuzixSensorListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * This Fragment is for use when you want to see the raw numbers coming back from the device
 * as it pertains to the Accelerometer, the Magnetometer, and the Gyrometer.
 *
 * For comparison purposes, it will also show values from the phone the app is running on.
 */
class SensorDemoFragment : Fragment(), VuzixSensorListener, SensorEventListener {

    lateinit var binding: FragmentSensorDemoBinding
    lateinit var sensors: Sensors

    lateinit var sensorManager: SensorManager
    lateinit var accelerometer: Sensor
    lateinit var gyrometer: Sensor
    lateinit var compass: Sensor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sensors = Sensors(requireContext(), this)
        try {
            sensors.connect()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        sensorManager = requireActivity().getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        gyrometer = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
        compass = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_sensor_demo, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (sensors.connected) {
            sensors.initializeSensors()
        }
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
        sensorManager.registerListener(this, gyrometer, SensorManager.SENSOR_DELAY_NORMAL)
        sensorManager.registerListener(this, compass, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onStop() {
        super.onStop()
        sensors.disconnect()
        sensorManager.unregisterListener(this, accelerometer)
        sensorManager.unregisterListener(this, gyrometer)
        sensorManager.unregisterListener(this, compass)
    }

    override fun onError(error: Exception) {
        GlobalScope.launch(Dispatchers.Main) {
            AlertDialog.Builder(requireContext())
                .setTitle("Error")
                .setMessage(error.message)
                .setNeutralButton("OK") { _, _ -> /* Do Nothing */}
                .show()
        }
    }

    override fun onSensorChanged(event: VuzixSensorEvent) {
        when (event.sensorType) {
            Sensor.TYPE_ACCELEROMETER -> {
                GlobalScope.launch(Dispatchers.Main) {
                    binding.tvHidAccelX?.text = event.values[0].toString()
                    binding.tvHidAccelY?.text = event.values[1].toString()
                    binding.tvHidAccelZ?.text = event.values[2].toString()
                }
            }
            Sensor.TYPE_MAGNETIC_FIELD -> {
                GlobalScope.launch(Dispatchers.Main) {
                    val checkMagValues = event.values.toList()
                    if (checkMagValues.isNotEmpty()) {
                        binding.tvHidMagX?.text = event.values[0].toString()
                        binding.tvHidMagY?.text = event.values[1].toString()
                        binding.tvHidMagZ?.text = event.values[2].toString()
                    }
                }
            }
            Sensor.TYPE_GYROSCOPE -> {
                GlobalScope.launch(Dispatchers.Main) {
                    binding.tvHidGyroX?.text = event.values[0].toString()
                    binding.tvHidGyroY?.text = event.values[1].toString()
                    binding.tvHidGyroZ?.text = event.values[2].toString()
                }
            }
            Sensor.TYPE_GAME_ROTATION_VECTOR -> {
//                GlobalScope.launch(Dispatchers.Main) {
//                    binding.tvHidQuadX?.text = event.values[0].toString()
//                    binding.tvHidQuadY?.text = event.values[1].toString()
//                    binding.tvHidQuadZ?.text = event.values[2].toString()
//                    binding.tvHidQuadW?.text = event.values[3].toString()
//                }
            }
        }
    }

    override fun onSensorInitialized() {
    }

    override fun onSensorChanged(event: SensorEvent) {
        when (event.sensor.type) {
            Sensor.TYPE_ACCELEROMETER -> {
                GlobalScope.launch(Dispatchers.Main) {
                    binding.tvHidQuadX?.text = event.values[0].toString()
                    binding.tvHidQuadY?.text = event.values[1].toString()
                    binding.tvHidQuadZ?.text = event.values[2].toString()
                }
            }
            Sensor.TYPE_MAGNETIC_FIELD -> {
                Timber.d("X: ${event.values[0]}\nY: ${event.values[1]}\nZ: ${event.values[2]}")
                GlobalScope.launch(Dispatchers.Main) {
                    binding.tvHidPmagX?.text = event.values[0].toString()
                    binding.tvHidPmagY?.text = event.values[1].toString()
                    binding.tvHidPmagZ?.text = event.values[2].toString()
                }
            }
            Sensor.TYPE_GYROSCOPE -> {
                GlobalScope.launch(Dispatchers.Main) {
                    binding.tvHidPgyroX?.text = event.values[0].toString()
                    binding.tvHidPgyroY?.text = event.values[1].toString()
                    binding.tvHidPgyroZ?.text = event.values[2].toString()
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Nothing
    }

}