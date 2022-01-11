
package com.vuzix.android.m400c.hid.sensors;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.usb.UsbManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

//import com.vuzix.m400cconnectivitysdk.sensor.VuzixSensor;
//import com.vuzix.m400cconnectivitysdk.sensor.VuzixSensorEvent;
//import com.vuzix.m400cconnectivitysdk.sensor.VuzixSensorEventListener;
//import com.vuzix.m400cconnectivitysdk.sensor.VuzixSensorManager;

import com.vuzix.m400cconnectivitysdk.sensor.VuzixSensor;
import com.vuzix.m400cconnectivitysdk.sensor.VuzixSensorEvent;
import com.vuzix.m400cconnectivitysdk.sensor.VuzixSensorEventListener;
import com.vuzix.m400cconnectivitysdk.sensor.VuzixSensorManager;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;

public class Orientation implements SensorEventListener, VuzixSensorEventListener {

  public interface Listener {
    void onOrientationChanged(float azimuth, float pitch, float roll);
  }
  private static final String TAG = "ORIENTATION";
  private static final int SENSOR_DELAY_MICROS = 16 * 1000; // 16ms
  private static final int SMOOTHING_COUNT = 20;

//  private final WindowManager mWindowManager;

//  private final SensorManager mSensorManager;

  @Nullable
  //private final Sensor mRotationSensor;
  private final VuzixSensorManager vuzixSensorManager;

  private int mLastAccuracy;
  private Listener mListener;
  private float[] acc_values;
  private float[] gyro_values;
  private float[] mag_values;
  private LinkedList<float[]> acc_buffer = new LinkedList<float[]>();
  private LinkedList<float[]> mag_buffer = new LinkedList<float[]>();


  public Orientation(/*Activity activity,*/ UsbManager usbManager) {
//    mWindowManager = activity.getWindow().getWindowManager();
    //mSensorManager = (SensorManager) context.getSystemService(Activity.SENSOR_SERVICE);
    vuzixSensorManager = new VuzixSensorManager(usbManager);
    for (int i = 0; i < SMOOTHING_COUNT; i++) {
      acc_buffer.add(new float[]{0f, 0f, 0f});
      mag_buffer.add(new float[]{0f, 0f, 0f});
    }

    // Can be null if the sensor hardware is not available
//    mRotationSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR);
  }

  public void startListening(Listener listener) {
    if (mListener == listener) {
      return;
    }
    mListener = listener;
//    if (mRotationSensor == null) {
//      LogUtil.w("Rotation vector sensor not available; will not provide orientation data.");
//      return;
//    }
    //mSensorManager.registerListener(this, mRotationSensor, SENSOR_DELAY_MICROS);
    vuzixSensorManager.registerListener(this);
//    vuzixSensorManager.start(new Continuation<Unit>() {
//      @Override
//      public CoroutineContext getContext() {
//        return EmptyCoroutineContext.INSTANCE;
//      }
//
//      @Override
//      public void resumeWith(Object obj) {
//      }
//    });
  }

  public void stopListening() {
    //mSensorManager.unregisterListener(this);
    if (vuzixSensorManager != null) {
      vuzixSensorManager.stopSensorStream();
    }
    mListener = null;
  }

  @Override
  public void onAccuracyChanged(Sensor sensor, int accuracy) {
    if (mLastAccuracy != accuracy) {
      mLastAccuracy = accuracy;
    }
  }
  @Override
  public void onAccuracyChanged(@NonNull VuzixSensor vuzixSensor, int accuracy) {

  }

  @Override
  public void onSensorChanged(SensorEvent event) {
    if (mListener == null) {
      return;
    }
    if (mLastAccuracy == SensorManager.SENSOR_STATUS_UNRELIABLE) {
      Log.i(TAG, "Sensor is unreliable");
      return;
    }
    switch (event.sensor.getType()) {
      case Sensor.TYPE_ACCELEROMETER:
        acc_values = event.values;
        Log.i(TAG, "ACCEL: " + Arrays.toString(acc_values));
        updateOrientationHW(acc_values, mag_values);
        break;
      case Sensor.TYPE_GYROSCOPE:
        gyro_values = event.values;
        Log.i(TAG, "GYRO: " + Arrays.toString(gyro_values));
        break;
      case Sensor.TYPE_MAGNETIC_FIELD:
        mag_values = event.values;
        Log.i(TAG, "MAG: " + Arrays.toString(mag_values));
        break;
      case Sensor.TYPE_ROTATION_VECTOR:
        Log.i(TAG, "ROT: " + Arrays.toString(event.values));
        break;
      case Sensor.TYPE_GAME_ROTATION_VECTOR:
        Log.i(TAG, "GAME_ROT: " + Arrays.toString(event.values));
        //updateOrientation(sensorEvent.getValues());
        break;
    }
  }

  @Override
  public void onSensorChanged(@NonNull VuzixSensorEvent sensorEvent) {
    switch (sensorEvent.getSensor().getType()) {
      case Sensor.TYPE_ACCELEROMETER:
        acc_values = sensorEvent.getValues();
        //Log.i(TAG, "ACCEL: " + Arrays.toString(acc_values));
        updateOrientationHW(acc_values, mag_values);
        break;
      case Sensor.TYPE_GYROSCOPE:
        gyro_values = sensorEvent.getValues();
        Log.i(TAG, "GYRO: " + Arrays.toString(gyro_values));
        break;
      case Sensor.TYPE_MAGNETIC_FIELD:
        mag_values = sensorEvent.getValues();
        //Log.i(TAG, "MAG: " + Arrays.toString(mag_values));
        break;
      case Sensor.TYPE_ROTATION_VECTOR:
        Log.i(TAG, "ROT: " + Arrays.toString(sensorEvent.getValues()));
        break;
      case Sensor.TYPE_GAME_ROTATION_VECTOR:
        Log.i(TAG, "GAME_ROT: " + Arrays.toString(sensorEvent.getValues()));
        //updateOrientation(sensorEvent.getValues());
        break;
    }
  }

  @SuppressWarnings("SuspiciousNameCombination")
  private void updateOrientation(float[] rotationVector) {
    float[] rotationMatrix = new float[9];
    SensorManager.getRotationMatrixFromVector(rotationMatrix, rotationVector);
    //Log.i(TAG, "SensorManager rotation matrix: " + Arrays.toString(rotationMatrix));
    rotationMatrix = normalize(rotationMatrix);
    //Log.i(TAG, "SensorManager normalized rotation matrix: " + Arrays.toString(rotationMatrix));

    // Transform rotation matrix into azimuth/pitch/roll
    float[] orientation = new float[3];
    SensorManager.getOrientation(/*adjustedR*/rotationMatrix, orientation);
    // Convert radians to degrees
    float azimuth = orientation[0] * -57;
    float pitch = orientation[1] * -57;
    float roll = orientation[2] * -57;

    mListener.onOrientationChanged(azimuth, pitch, roll);
  }

  private void updateOrientationHW(float acc[], float mag[]) {
    float[] rotationMatrix = new float[9];
    float[] accAvg = new float[3];
    float[] magAvg = new float[3];
    smoothOrientation(acc, mag, accAvg, magAvg);
    SensorManager.getRotationMatrix(rotationMatrix, null, accAvg, magAvg);
    //Log.i(TAG, "SensorManager HW rotation matrix: " + Arrays.toString(rotationMatrix));
    rotationMatrix = normalize(rotationMatrix);
    //Log.i(TAG, "SensorManager HW normalized rotation matrix: " + Arrays.toString(rotationMatrix));

    float[] orientation = new float[3];
    SensorManager.getOrientation(rotationMatrix, orientation);
    // Convert radians to degrees
    float azimuth = orientation[0] * -57;
    float pitch = orientation[1] * 57;
    float roll = orientation[2] * -57;

    if (mListener != null) {
      mListener.onOrientationChanged(azimuth, pitch, roll);
    }
  }

  public static float[] normalize(float[] data) {
    float[] tmp = new float[data.length];
    float max = 0;
    for (int i = 0; i < data.length; i++) {
      float t = Math.abs(data[i]);
      if (t > max) {
        max = t;
      }
    }
    //float scale = 32677.0f / max;
    for (int i = 0; i < data.length; i++) {
      tmp[i] = (((float) data[i]) /max);
    }
    return tmp;
  }

  private void smoothOrientation(float[] acc, float[] mag, float[] acc_avg, float[] mag_avg) {
    float a[] = {0f, 0f, 0f};
    float m[] = {0f, 0f, 0f};
    //Log.i(TAG, "Smoothing acc: " + Arrays.toString(acc) + ", size = " + acc_buffer.size());
    //Log.i(TAG, "Smoothing mag: " + Arrays.toString(mag) + ", size = " + mag_buffer.size());
    if (acc == null || mag == null) {
      acc_avg = acc;
      mag_avg = mag;
      return;
    }
    int accSize = acc_buffer.size();
    int magSize = mag_buffer.size();
    acc_buffer.pollFirst();
    acc_buffer.addLast(acc);
    mag_buffer.pollFirst();
    mag_buffer.addLast(mag);

    Iterator<float[]> accIter = acc_buffer.iterator();
    while (accIter.hasNext()){
      float tmp[] = accIter.next();
      a[0] += tmp[0];
      a[1] += tmp[1];
      a[2] += tmp[2];
    }
    Iterator<float[]> magIter = mag_buffer.iterator();
    while (magIter.hasNext()){
      float tmp[] = magIter.next();
      m[0] += tmp[0];
      m[1] += tmp[1];
      m[2] += tmp[2];
    }

    a[0] /= accSize;
    a[1] /= accSize;
    a[2] /= accSize;
    m[0] /= magSize;
    m[1] /= magSize;
    m[2] /= magSize;
    acc_avg[0] = a[0];
    acc_avg[1] = a[1];
    acc_avg[2] = a[2];
    mag_avg[0] = m[0];
    mag_avg[1] = m[1];
    mag_avg[2] = m[2];
    //Log.i(TAG, "Smoothed acc: " + Arrays.toString(acc_avg));
    //Log.i(TAG, "Smoothed mag: " + Arrays.toString(mag_avg));
  }
}
