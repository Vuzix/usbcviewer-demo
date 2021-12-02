package com.vuzix.android.m400c.hid.di

import com.vuzix.android.m400c.common.domain.entity.VuzixHidDevice
import com.vuzix.android.m400c.core.util.M400cConstants
import com.vuzix.android.m400c.hid.data.model.HidSensorInterface
import com.vuzix.android.m400c.hid.data.model.ViewerControlInterface
import com.vuzix.android.m400c.hid.data.model.ViewerKeyboardInterface
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object HidModule {

    @Singleton
    @Provides
    fun provideViewerControlInterface(device: VuzixHidDevice): ViewerControlInterface {
        return device.usbDevice.let {
            val intf = it!!.getInterface(M400cConstants.HID_VIEWER_CONTROL)
            val outboundEndpoint = intf.getEndpoint(M400cConstants.HID_VIEWER_CONTROL_OUTBOUND)
            val inboundEndpoint = intf.getEndpoint(M400cConstants.HID_VIEWER_CONTROL_INBOUND)
            ViewerControlInterface(intf, inboundEndpoint)
        }
    }

    @Singleton
    @Provides
    fun provideViewerKeyboardInterface(device: VuzixHidDevice): ViewerKeyboardInterface {
        return device.usbDevice.let {
            val intf = it!!.getInterface(M400cConstants.HID_VIEWER_KEYBOARD)
            val inboundEndpoint = intf.getEndpoint(M400cConstants.HID_VIEWER_KEYBOARD_INBOUND)
            ViewerKeyboardInterface(intf, inboundEndpoint)
        }
    }

    @Singleton
    @Provides
    fun provideHidSensorInterface(device: VuzixHidDevice): HidSensorInterface {
        return device.usbDevice.let {
            val intf = it!!.getInterface(M400cConstants.HID_SENSOR)
            val inboundEndpoint = intf.getEndpoint(M400cConstants.HID_SENSOR_INBOUND)
            HidSensorInterface(intf, inboundEndpoint)
        }
    }

}