package com.vuzix.android.camerasdk;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.usb.UsbDevice;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;

import com.vuzix.android.camerasdk.callbacks.ConnectCallback;
import com.vuzix.android.camerasdk.callbacks.IButtonCallback;
import com.vuzix.android.camerasdk.callbacks.IFrameCallback;
import com.vuzix.android.camerasdk.callbacks.PhotographCallback;
import com.vuzix.android.camerasdk.callbacks.PictureCallback;
import com.vuzix.android.camerasdk.callbacks.PreviewCallback;
import com.vuzix.android.camerasdk.camera.Size;
import com.vuzix.android.camerasdk.camera.UVCCamera;
import com.vuzix.android.camerasdk.configs.CameraConfig;
import com.vuzix.android.camerasdk.usb.UsbMonitor;
import com.vuzix.android.camerasdk.utils.FileUtil;
import com.vuzix.android.camerasdk.utils.LogUtil;
import com.vuzix.android.camerasdk.utils.RxUtil;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

public class UVCCameraProxy implements IUVCCamera {
    private static int PICTURE_WIDTH = 640;
    private static int PICTURE_HEIGHT = 480;
    private Context mContext;
    private UsbMonitor mUsbMonitor;
    protected UVCCamera mUVCCamera;
    private View mPreviewView;
    private Surface mSurface;
    private PictureCallback mPictureCallback;
    private PhotographCallback mPhotographCallback;
    private PreviewCallback mPreviewCallback;
    private ConnectCallback mConnectCallback;
    protected CompositeSubscription mSubscriptions;
    private CameraConfig mConfig;
    protected float mPreviewRotation;
    protected boolean isTakePhoto;
    private String mPictureName;

    public UVCCameraProxy(Context context) {
        mContext = context;
        mConfig = new CameraConfig();
        mUsbMonitor = new UsbMonitor(context, mConfig);
        mSubscriptions = new CompositeSubscription();
    }

    @Override
    public void registerReceiver() {
        mUsbMonitor.registerReceiver();
    }

    @Override
    public void unregisterReceiver() {
        mUsbMonitor.unregisterReceiver();
    }

    @Override
    public void checkDevice() {
        mUsbMonitor.checkDevice();
    }

    @Override
    public void requestPermission(UsbDevice usbDevice) {
        mUsbMonitor.requestPermission(usbDevice);
    }

    @Override
    public void connectDevice(UsbDevice usbDevice) {
        mUsbMonitor.connectDevice(usbDevice);
    }

    @Override
    public void closeDevice() {
        mUsbMonitor.closeDevice();
    }

    @Override
    public void openCamera() {
        try {
            mUVCCamera = new UVCCamera();
            mUVCCamera.open(mUsbMonitor.getUsbController());
            LogUtil.i("openCamera");
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (mUVCCamera != null && mConnectCallback != null) {
            mConnectCallback.onCameraOpened();
        }
    }

    @Override
    public void closeCamera() {
        try {
            if (mUVCCamera != null) {
                mUVCCamera.destroy();
                mUVCCamera = null;
            }
            mUsbMonitor.closeDevice();
            LogUtil.i("closeCamera");
        } catch (Exception e) {
            e.printStackTrace();
        }
        mSubscriptions.clear();
    }

    @Override
    public void setPreviewSurface(SurfaceView surfaceView) {
        this.mPreviewView = surfaceView;
        if (surfaceView != null && surfaceView.getHolder() != null) {
            surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder holder) {
                    LogUtil.i("surfaceCreated");
                    mSurface = holder.getSurface();
                    checkDevice();
                    registerReceiver();
                }

                @Override
                public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                    LogUtil.i("surfaceChanged");
                }

                @Override
                public void surfaceDestroyed(SurfaceHolder holder) {
                    LogUtil.i("surfaceDestroyed");
                    mSurface = null;
                    unregisterReceiver();
                    closeCamera();
                }
            });
        }
    }

    @Override
    public void setPreviewTexture(TextureView textureView) {
        this.mPreviewView = textureView;
        if (textureView != null) {
            if (mPreviewRotation != 0) {
                textureView.setRotation(mPreviewRotation);
            }
            textureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
                @Override
                public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                    LogUtil.i("onSurfaceTextureAvailable");
                    mSurface = new Surface(surface);
                    checkDevice();
                    registerReceiver();
                }

                @Override
                public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
                    LogUtil.i("onSurfaceTextureSizeChanged");
                }

                @Override
                public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                    LogUtil.i("onSurfaceTextureDestroyed");
                    mSurface = null;
                    unregisterReceiver();
                    closeCamera();
                    return false;
                }

                @Override
                public void onSurfaceTextureUpdated(SurfaceTexture surface) {
                }
            });
        }
    }

    @Override
    public void setPreviewRotation(float rotation) {
        if (mPreviewView != null && mPreviewView instanceof TextureView) {
            this.mPreviewRotation = rotation;
            mPreviewView.setRotation(rotation);
        }
    }

    @Override
    public void setPreviewDisplay(Surface surface) {
        mSurface = surface;
        try {
            if (mUVCCamera != null && mSurface != null) {
                mUVCCamera.setPreviewDisplay(mSurface);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setPreviewSize(int width, int height) {
        try {
            if (mUVCCamera != null) {
                this.PICTURE_WIDTH = width;
                this.PICTURE_HEIGHT = height;
                mUVCCamera.setPreviewSize(width, height);
                LogUtil.i("setPreviewSize-->" + width + " * " + height);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Size getPreviewSize() {
        try {
            if (mUVCCamera != null) {
                return mUVCCamera.getPreviewSize();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Size> getSupportedPreviewSizes() {
        try {
            if (mUVCCamera != null) {
                return mUVCCamera.getSupportedSizeList();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    @Override
    public void startPreview() {
        try {
            if (mUVCCamera != null) {
                LogUtil.i("startPreview");

                mSubscriptions.add(Observable.create(new Observable.OnSubscribe<Integer>() {
                    @Override
                    public void call(final Subscriber<? super Integer> subscriber) {
                        mUVCCamera.setButtonCallback(new IButtonCallback() {
                            @Override
                            public void onButton(int button, int state) {
                                LogUtil.i("button-->" + button + " state-->" + state);
                                if (button == 1 && state == 0) {
                                    subscriber.onNext(state);
                                }
                            }
                        });
                    }
                }).compose(RxUtil.<Integer>io_main()).subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        if (mPhotographCallback != null) {
                            mPhotographCallback.onPhotographClick();
                        }
                    }
                }));

                mUVCCamera.setFrameCallback(new IFrameCallback() {
                    @Override
                    public void onFrame(ByteBuffer frame) {
                        int lenght = frame.capacity();
                        byte[] yuv = new byte[lenght];
                        frame.get(yuv);
                        if (mPreviewCallback != null) {
                            mPreviewCallback.onPreviewFrame(yuv);
                        }
                        if (isTakePhoto) {
                            LogUtil.i("take picture");
                            isTakePhoto = false;
                            savePicture(yuv, PICTURE_WIDTH, PICTURE_HEIGHT, mPreviewRotation);
                        }
                    }
                }, UVCCamera.PIXEL_FORMAT_YUV420SP);

                if (mSurface != null) {
                    mUVCCamera.setPreviewDisplay(mSurface);
                }
                mUVCCamera.updateCameraParams();
                mUVCCamera.startPreview();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void savePicture(final byte[] yuv, final int width, final int height, final float rotation) {
        if (mPictureCallback == null) {
            return;
        }
        LogUtil.i("savePicture");
        mSubscriptions.add(Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                File file = getPictureFile(mPictureName);
                String path = FileUtil.saveYuv2Jpeg(file, yuv, width, height, rotation);
                subscriber.onNext(path);
            }
        }).compose(RxUtil.<String>io_main()).subscribe(new Action1<String>() {
            @Override
            public void call(String path) {
                if (mPictureCallback != null) {
                    mPictureCallback.onPictureTaken(path);
                }
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                if (mPictureCallback != null) {
                    mPictureCallback.onPictureTaken(null);
                }
            }
        }));
    }

    @Override
    public void stopPreview() {
        try {
            if (mUVCCamera != null) {
                LogUtil.i("stopPreview");
                mUVCCamera.setButtonCallback(null);
                mUVCCamera.setFrameCallback(null, 0);
                mUVCCamera.stopPreview();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void takePicture() {
        isTakePhoto = true;
        mPictureName = UUID.randomUUID().toString() + ".jpg";
    }

    @Override
    public void takePicture(String pictureName) {
        isTakePhoto = true;
        mPictureName = pictureName;
    }

    @Override
    public void setConnectCallback(ConnectCallback callback) {
        this.mConnectCallback = callback;
        this.mUsbMonitor.setConnectCallback(callback);
    }

    @Override
    public void setPreviewCallback(PreviewCallback callback) {
        this.mPreviewCallback = callback;
    }

    @Override
    public void setPhotographCallback(PhotographCallback callback) {
        this.mPhotographCallback = callback;
    }

    @Override
    public void setPictureTakenCallback(PictureCallback callback) {
        this.mPictureCallback = callback;
    }

    @Override
    public UVCCamera getUVCCamera() {
        return mUVCCamera;
    }

    @Override
    public boolean isCameraOpen() {
        return mUVCCamera != null;
    }

    @Override
    public CameraConfig getConfig() {
        return mConfig;
    }

    @Override
    public void clearCache() {
        try {
            File cacheDir = new File(FileUtil.getDiskCacheDir(mContext, mConfig.getDirName()));
            FileUtil.deleteFile(cacheDir);

            File sdcardDir = FileUtil.getSDCardDir(mConfig.getDirName());
            FileUtil.deleteFile(sdcardDir);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected File getPictureFile(String pictureName) {
        File file = null;
        switch (mConfig.getPicturePath()) {
            case APPCACHE:
            default:
                file = FileUtil.getCacheFile(mContext, mConfig.getDirName(), pictureName);
                break;

            case SDCARD:
                file = FileUtil.getSDCardFile(mConfig.getDirName(), pictureName);
                break;
        }
        return file;
    }

}
