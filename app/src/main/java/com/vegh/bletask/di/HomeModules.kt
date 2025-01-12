package com.vegh.bletask.di

import android.content.Context
import com.vegh.bletask.utils.bleTools.BleDeviceManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import no.nordicsemi.android.support.v18.scanner.BluetoothLeScannerCompat
import no.nordicsemi.android.support.v18.scanner.ScanSettings
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object HomeModules {

    @Provides
    fun providerBleScanner() = BluetoothLeScannerCompat.getScanner()

    @Provides
    fun SettingScanner(): ScanSettings {
        return ScanSettings.Builder()
            .setLegacy(false)
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            .setReportDelay(500)
            .setUseHardwareBatchingIfSupported(true)
            .build()
    }

    @Singleton
    @Provides
    fun provideBleDeviceManager(@ApplicationContext appContext: Context): BleDeviceManager {
        return BleDeviceManager(context = appContext)
    }
}