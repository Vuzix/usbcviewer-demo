package com.vuzix.android.camerasdk;

import android.hardware.usb.UsbDevice;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.TextureView;

import com.vuzix.android.camerasdk.callbacks.ConnectCallback;
import com.vuzix.android.camerasdk.callbacks.PhotographCallback;
import com.vuzix.android.camerasdk.callbacks.PictureCallback;
import com.vuzix.android.camerasdk.callbacks.PreviewCallback;
import com.vuzix.android.camerasdk.camera.Size;
import com.vuzix.android.camerasdk.camera.UVCCamera;
import com.vuzix.android.camerasdk.configs.CameraConfig;

import java.util.List;

public interface IUVCCamera {
    void registerReceiver();
    void unregisterReceiver();
    void checkDevice();
    void requestPermission(UsbDevice usbDevice);
    void connectDevice(UsbDevice usbDevice);
    void closeDevice();
    void openCamera();
    void closeCamera();
    void setPreviewSurface(SurfaceView surfaceView);
    void setPreviewTexture(TextureView textureView);
    void setPreviewRotation(float rotation);
    void setPreviewDisplay(Surface surface);
    void setPreviewSize(int width, int height);
    Size getPreviewSize();
    List<Size> getSupportedPreviewSizes();
    void startPreview();
    void stopPreview();
    void takePicture();
    void takePicture(String pictureName);
    void setConnectCallback(ConnectCallback callback);
    void setPreviewCallback(PreviewCallback callback);
    void setPhotographCallback(PhotographCallback callback);
    void setPictureTakenCallback(PictureCallback callback);
    UVCCamera getUVCCamera();
    boolean isCameraOpen();
    CameraConfig getConfig();
    void clearCache();
}