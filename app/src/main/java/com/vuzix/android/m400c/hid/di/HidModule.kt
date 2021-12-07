package com.vuzix.android.m400c.hid.di

import com.vuzix.android.m400c.common.domain.entity.VuzixHidDevice
import com.vuzix.android.m400c.core.util.M400cConstants
import com.vuzix.android.m400c.hid.data.model.HidSensorInterface
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
    fun provideHidSensorInterface(device: VuzixHidDevice): HidSensorInterface {
        return device.usbDevice.let {
            val intf = it!!.getInterface(M400cConstants.HID_SENSOR)
            val inboundEndpoint = intf.getEndpoint(M400cConstants.HID_SENSOR_INBOUND)
            HidSensorInterface(intf, inboundEndpoint)
        }
    }

}