package com.vuzix.android.camerasdk.utils;

public interface CustomLogger {
    void d(String tag, String content);

    void d(String tag, String content, Throwable tr);

    void e(String tag, String content);

    void e(String tag, String content, Throwable tr);

    void i(String tag, String content);

    void i(String tag, String content, Throwable tr);

    void v(String tag, String content);

    void v(String tag, String content, Throwable tr);

    void w(String tag, String content);

    void w(String tag, String content, Throwable tr);

    void w(String tag, Throwable tr);

    void wtf(String tag, String content);

    void wtf(String tag, String content, Throwable tr);

    void wtf(String tag, Throwable tr);
}
