package com.vegh.bletask.utils.bleTools

import android.bluetooth.BluetoothDevice
import android.util.Log
import no.nordicsemi.android.ble.callback.profile.ProfileDataCallback
import no.nordicsemi.android.ble.data.Data

abstract class DeviceDataCallback : ProfileDataCallback {

    override fun onDataReceived(device: BluetoothDevice, data: Data) {
        if (data.size() == 0) {
            onInvalidDataReceived(device, data)
        } else {
            onDataReceived(device, data.value)
        }
    }

    override fun onInvalidDataReceived(device: BluetoothDevice, data: Data) {
        Log.w(this::class.simpleName, "Invalid data received: $data")
    }

    abstract fun onDataReceived(device: BluetoothDevice?, data: ByteArray?)
}
