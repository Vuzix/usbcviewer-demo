package com.vuzix.sdk.usbcviewer.util

import android.util.Log
import com.vuzix.sdk.usbcviewer.BuildConfig

object LogUtil {

    private val explicitTag = ThreadLocal<String>()
    private val tag: String?
        get() {
            val tag = explicitTag.get()
            if (tag != null) {
                explicitTag.remove()
            }
            return tag
        }

    /**
     * Function used for debug logging when you don't want these messages to show up
     * in the release product. Explicitly writes with Debug Level Priority.
     *
     * @param message The message to be displayed.
     */
    fun debug(message: String) {
        if (BuildConfig.DEBUG) {
            Log.d(tag, message)
        }
    }

    /**
     * Function used for release logging when you want something to show up in the
     * release product. Explicitly writes with Info Level Priority.
     */
    fun rel(message: String) {
        Log.i(tag, message)
    }
}