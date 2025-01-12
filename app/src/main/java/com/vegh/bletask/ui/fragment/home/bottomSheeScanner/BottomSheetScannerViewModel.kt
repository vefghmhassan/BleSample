package com.vegh.bletask.ui.fragment.home.bottomSheeScanner

import android.bluetooth.le.BluetoothLeScanner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vegh.bletask.utils.bleTools.ScannerCallbackHandler
import no.nordicsemi.android.support.v18.scanner.*

class
BottomSheetScannerViewModel() : ViewModel() {

    var list = MutableLiveData<List<ScanResult>>()
    var scanner:BluetoothLeScannerCompat?=null


    init {
          scanner = BluetoothLeScannerCompat.getScanner()

    }
    var callback = ScannerCallbackHandler(list)

    fun startScanner() {
        val setting = ScanSettings.Builder()

            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            .setReportDelay(2000)
            .setUseHardwareBatchingIfSupported(false)
            .setUseHardwareFilteringIfSupported(false)
            .build()
        val filters: List<ScanFilter> = ArrayList()
        if (scanner != null) {
            scanner?.stopScan(callback)
            scanner?.startScan(filters, setting, callback)
        } else {
            scanner = BluetoothLeScannerCompat.getScanner()
            scanner?.startScan(filters, setting, callback)
        }

    }

    fun stopScan() {
        scanner?.stopScan(callback)
    }
}