package com.vuzix.android.m400c.video.di

import com.vuzix.android.m400c.common.domain.entity.VuzixVideoDevice
import com.vuzix.android.m400c.core.util.M400cConstants
import com.vuzix.android.m400c.core.util.M400cConstants.VIDEO_CONTROL_ENDPOINT_ONE
import com.vuzix.android.m400c.core.util.M400cConstants.VIDEO_STREAM_ENDPOINT_ONE
import com.vuzix.android.m400c.video.data.InboundVideoInterfaceOne
import com.vuzix.android.m400c.video.data.InboundVideoInterfaceTwo
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object VideoModule {

    @Singleton
    @Provides
    fun provideInboundVideoInterfaceOne(device: VuzixVideoDevice): InboundVideoInterfaceOne {
        return device.usbDevice.let {
            val intf = it!!.getInterface(M400cConstants.VIDEO_CONTROL)
            val inboundEndpoint = intf.getEndpoint(VIDEO_CONTROL_ENDPOINT_ONE)
            InboundVideoInterfaceOne(intf, inboundEndpoint)
        }
    }

    @Singleton
    @Provides
    fun provideInboundVideoInterfaceTwo(device: VuzixVideoDevice): InboundVideoInterfaceTwo {
        return device.usbDevice.let {
            val intf = it!!.getInterface(M400cConstants.VIDEO_STREAM)
            val inboundEndpoint = intf.getEndpoint(VIDEO_STREAM_ENDPOINT_ONE)
            InboundVideoInterfaceTwo(intf, inboundEndpoint)
        }
    }
}