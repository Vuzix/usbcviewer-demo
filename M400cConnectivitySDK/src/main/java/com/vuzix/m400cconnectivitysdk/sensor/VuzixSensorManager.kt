package com.vuzix.m400cconnectivitysdk.sensor

import android.hardware.Sensor.*
import android.hardware.usb.UsbManager
import android.util.Log
import com.vuzix.m400cconnectivitysdk.M400cConstants
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flatMapConcat
import kotlin.math.sqrt

private const val EPSILON = 0.000000001f

class VuzixSensorManager(usbManager: UsbManager) {
    private val TAG = "VuzixSensorManager"
    private var hidDataSource: HidSensorDataSource? = null
    private lateinit var vuzixHidDevice: VuzixHidDevice
    private lateinit var hidSensorInterface: HidSensorInterface
    private var coroutineScope: CoroutineScope =
        CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private var sensorEventListener: VuzixSensorEventListener? = null

    init {
        if (usbManager.deviceList.isNotEmpty()) {
            vuzixHidDevice = usbManager.let {
                val devices = it.deviceList
                val device =
                    devices.values.first { device -> device.productId == M400cConstants.HID_PID && device.vendorId == M400cConstants.HID_VID }
                VuzixHidDevice(device)
            }
            hidSensorInterface = vuzixHidDevice.usbDevice.let {
                val intf = it!!.getInterface(M400cConstants.HID_SENSOR)
                val inboundEndpoint = intf.getEndpoint(M400cConstants.HID_SENSOR_INBOUND)
                HidSensorInterface(intf, inboundEndpoint)
            }
            hidDataSource = HidSensorDataSource(
                coroutineScope,
                usbManager,
                vuzixHidDevice,
                hidSensorInterface
            )
            coroutineScope.launch {
                hidDataSource?.let { dataSource ->
                    dataSource.initConnection()
                        .flatMapConcat {
                            dataSource.initSensor(M400cConstants.SENSOR_ACCELEROMETER_ID)
                        }
    //                .flatMapConcat {
    //                    hidDataSource!!.initSensor(M400cConstants.SENSOR_GYRO_ID)
    //                }
                        .flatMapConcat {
                            dataSource.initSensor(M400cConstants.SENSOR_MAGNETOMETER_ID)
                        }
    //                .flatMapConcat {
    //                    hidDataSource!!.initSensor(M400cConstants.SENSOR_ORIENTATION_ID)
    //                }
                        .collect {
                            start()
                        }
                }
            }
        }
    }

    fun registerListener(sensorEventListener: VuzixSensorEventListener) {
        this.sensorEventListener = sensorEventListener
    }

    suspend fun start() {
        coroutineScope {
            launch {
                Log.i(TAG, "Launching sensor data read routine")
                startSensorStream()
            }
        }
    }

    private suspend fun startSensorStream() {
        hidDataSource?.let { dataSource ->
            coroutineScope {
                launch {
                    dataSource.startStream()
                    dataSource.dataFlow.collect { data ->
                        data.handle(
                            onFailure = { println("error") },
                            onSuccess = {
                                val vuzixSensorEvent = VuzixSensorEvent(VuzixSensor())
                                val floatArray = FloatArray(4)
                                //Log.i(TAG, "HID data: " + Arrays.toString(it)/*it[0].toInt()*/)
                                floatArray[0] = (((it[4].toInt() shl 8) or it[3].toInt()).toFloat())
                                floatArray[1] = (((it[6].toInt() shl 8) or it[5].toInt()).toFloat())
                                floatArray[2] = (((it[8].toInt() shl 8) or it[7].toInt()).toFloat())


                                when (it[0].toInt()) {
                                    M400cConstants.SENSOR_ACCELEROMETER_ID -> {
                                        vuzixSensorEvent.sensor.setType(
                                            TYPE_ACCELEROMETER
                                        )
                                        //Log.i(TAG, "ACCEL: " + Arrays.toString(floatArray))
                                    }
                                    M400cConstants.SENSOR_GYRO_ID -> {
                                        vuzixSensorEvent.sensor.setType(TYPE_GYROSCOPE)

                                        var axisX: Float = floatArray[0]
                                        var axisY: Float = floatArray[1]
                                        var axisZ: Float = floatArray[2]

                                        val omegaMagnitude: Float =
                                            sqrt(axisX * axisX + axisY * axisY + axisZ * axisZ)

                                        if (omegaMagnitude > EPSILON) {
                                            axisX /= omegaMagnitude
                                            axisY /= omegaMagnitude
                                            axisZ /= omegaMagnitude
                                        }

                                        floatArray[0] = axisX
                                        floatArray[1] = axisY
                                        floatArray[2] = axisZ
                                    }
                                    M400cConstants.SENSOR_MAGNETOMETER_ID -> {
                                        vuzixSensorEvent.sensor.setType(
                                            TYPE_MAGNETIC_FIELD
                                        )
                                    }
                                    M400cConstants.SENSOR_ORIENTATION_ID -> {
                                        floatArray[3] =
                                            (((it[10].toInt() shl 8) or it[9].toInt()).toFloat()) / 10 // Rotation sensor uses 4 shorts, other sensors only use 3
                                        vuzixSensorEvent.sensor.setType(
                                            TYPE_GAME_ROTATION_VECTOR
                                        )
                                        var q0: Float = floatArray[0]
                                        var q1: Float = floatArray[1]
                                        var q2: Float = floatArray[2]
                                        var q3: Float = floatArray[3]
                                        //Log.i(TAG, "ROT quat data: [" + q0 + ", " + q1 + ", " + q2 + ", " + q3 + "]")
                                    }
                                }
                                vuzixSensorEvent.values = floatArray
                                sensorEventListener?.onSensorChanged(vuzixSensorEvent)
                            }
                        )
                    }
                }
            }
        } ?: run {
            return
        }
    }

    fun stopSensorStream() {
        hidDataSource?.stopStream()
    }
}
