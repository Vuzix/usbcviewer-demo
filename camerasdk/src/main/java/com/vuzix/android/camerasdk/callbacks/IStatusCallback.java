package com.vuzix.android.camerasdk.callbacks;

import java.nio.ByteBuffer;

public interface IStatusCallback {
    void onStatus(int statusClass, int event, int selector, int statusAttribute, ByteBuffer data);
}
