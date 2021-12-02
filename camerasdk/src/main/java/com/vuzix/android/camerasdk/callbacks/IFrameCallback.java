package com.vuzix.android.camerasdk.callbacks;

import java.nio.ByteBuffer;

public interface IFrameCallback {
    void onFrame(ByteBuffer frame);
}
