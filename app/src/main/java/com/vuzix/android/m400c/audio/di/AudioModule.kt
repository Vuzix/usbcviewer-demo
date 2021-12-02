package com.vuzix.android.m400c.audio.di

import com.vuzix.android.m400c.audio.data.InboundAudioInterface
import com.vuzix.android.m400c.common.domain.entity.VuzixAudioDevice
import com.vuzix.android.m400c.core.util.M400cConstants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AudioModule {

    @Singleton
    @Provides
    fun provideInboundAudioInterface(device: VuzixAudioDevice): InboundAudioInterface {
        return device.usbDevice.let {
            val intf = it!!.getInterface(M400cConstants.MIC_STREAM)
            val inboundEndpoint = intf.getEndpoint(M400cConstants.MIC_STREAM_ENDPOINT_TWO)
            InboundAudioInterface(intf, inboundEndpoint)
        }
    }
}