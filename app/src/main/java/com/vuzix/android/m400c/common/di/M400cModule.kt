package com.vuzix.android.m400c.common.di

import android.content.Context
import android.hardware.usb.UsbManager
import com.vuzix.android.m400c.common.domain.entity.VuzixAudioDevice
import com.vuzix.android.m400c.common.domain.entity.VuzixHidDevice
import com.vuzix.android.m400c.common.domain.entity.VuzixVideoDevice
import com.vuzix.android.m400c.core.util.M400cConstants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object M400cModule {

    @Singleton
    @Provides
    fun provideUsbManager(@ApplicationContext context: Context): UsbManager = context.getSystemService(Context.USB_SERVICE) as UsbManager

    @Singleton
    @Provides
    fun provideHidDevice(usbManager: UsbManager): VuzixHidDevice {
        usbManager.let {
            val devices = it.deviceList
            val device = devices.values.first { device -> device.productId == M400cConstants.HID_PID && device.vendorId == M400cConstants.HID_VID }
            return VuzixHidDevice(device)
        }
    }

    @Singleton
    @Provides
    fun provideVideoDevice(usbManager: UsbManager): VuzixVideoDevice {
        usbManager.let {
            val devices = it.deviceList
            val device = devices.values.first { device -> device.productId == M400cConstants.VIDEO_PID && device.vendorId == M400cConstants.VIDEO_VID }
            return VuzixVideoDevice(device)
        }
    }

    @Singleton
    @Provides
    fun provideAudioDevice(usbManager: UsbManager): VuzixAudioDevice {
        usbManager.let {
            val devices = it.deviceList
            val device = devices.values.first { device -> device.productId == M400cConstants.AUDIO_PID && device.vendorId == M400cConstants.AUDIO_VID }
            return VuzixAudioDevice(device)
        }
    }

    @Singleton
    @Provides
    fun providesCoroutineScope(): CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

//    @Singleton
//    @Provides
//    fun provideGetDeviceUseCase(@ApplicationContext context: Context, usbManager: UsbManager): GetDeviceUseCase = GetDeviceUseCase(context, usbManager)

//    @Singleton
//    @Provides
//    fun provideSensorReadUseCase(): SensorReadUseCase = SensorReadUseCase()
}