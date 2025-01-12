package com.vegh.bletask.utils.bleTools

import androidx.lifecycle.MutableLiveData
import no.nordicsemi.android.support.v18.scanner.ScanCallback
import no.nordicsemi.android.support.v18.scanner.ScanResult

class ScannerCallbackHandler(var list: MutableLiveData<List<ScanResult>>) : ScanCallback() {
    override fun onScanResult(callbackType: Int, result: ScanResult) {
        super.onScanResult(callbackType, result)
    }

    override fun onBatchScanResults(results: MutableList<ScanResult>) {
        super.onBatchScanResults(results)
        list.postValue(results)

    }

    override fun onScanFailed(errorCode: Int) {
        super.onScanFailed(errorCode)
    }
}