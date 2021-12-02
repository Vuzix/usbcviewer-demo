package com.vuzix.android.camerasdk.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;

public class FileUtil {

    public static boolean hasExternalStorage() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    public static String getExternalStoragePath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }

    public static File getSDCardDir(String foderName) {
        if (!hasExternalStorage()) {
            return null;
        }
        return new File(getExternalStoragePath() + File.separator + foderName);
    }

    public static File getSDCardFile(String foderName, String fileName) {
        File foder = getSDCardDir(foderName);
        if (foder == null) {
            return null;
        }
        if (!foder.exists()) {
            if (!foder.mkdirs()) {
                return null;
            }
        }
        return new File(foder, fileName);
    }

    public static String getDiskCacheDir(Context context, String dirName) {
        String cachePath;
        if ((Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable())
                && context.getExternalCacheDir() != null) {
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            cachePath = context.getCacheDir().getPath();
        }
        return cachePath + File.separator + dirName;
    }

    public static File getCacheFile(Context context, String dirName, String fileName) {
        File dirFile = new File(getDiskCacheDir(context, dirName));
        if (!dirFile.exists()) {
            if (!dirFile.mkdirs()) {
                LogUtil.d("failed to create directory");
                return null;
            }
        }
        return new File(dirFile.getPath() + File.separator + fileName);
    }

    public static boolean deleteFile(File dirFile) {
        if (!dirFile.exists()) {
            return false;
        }
        if (dirFile.isFile()) {
            return dirFile.delete();
        } else {
            for (File file : dirFile.listFiles()) {
                deleteFile(file);
            }
        }
        return dirFile.delete();
    }

    public static String saveYuv2Jpeg(File file, byte[] yuv, int width, int height) {
        return saveBitmap(file, ImageUtil.yuv2Bitmap(yuv, width, height));
    }

    public static String saveYuv2Jpeg(File file, byte[] yuv, int width, int height, float rotation) {
        return saveBitmap(file, ImageUtil.yuv2Bitmap(yuv, width, height, rotation));
    }

    public static String saveBitmap(File file, Bitmap bitmap) {
        if (file == null || bitmap == null) {
            return null;
        }
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            if (bitmap != null && !bitmap.isRecycled()) {
                bitmap.recycle();
                bitmap = null;
            }
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file.getAbsolutePath();
    }

}
