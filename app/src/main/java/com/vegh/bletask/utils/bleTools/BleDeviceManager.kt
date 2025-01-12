package com.vegh.bletask.utils.bleTools

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattServer
import android.content.Context
import android.util.Log
import androidx.core.os.unregisterForAllProfilingResults
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import no.nordicsemi.android.ble.BleManager
import java.util.UUID

class BleDeviceManager(context: Context) : BleManager(context) {

    private val _ecgDataFlow = MutableSharedFlow<ByteArray>(replay = 1)
    val ecgDataFlow = _ecgDataFlow.asSharedFlow()

    private val _batteryData = MutableLiveData<ByteArray>()
    val batteryData: LiveData<ByteArray> get() = _batteryData

    private val _deviceNameFlow = MutableSharedFlow<String>(replay = 1)
    val deviceNameFlow = _deviceNameFlow.asSharedFlow()

    private var ecgCharacteristic: BluetoothGattCharacteristic? = null
    private var batteryCharacteristic: BluetoothGattCharacteristic? = null

    private val _connectionState = MutableStateFlow<BleConnectionState>(BleConnectionState.Disconnected)
    val connectionState = _connectionState.asStateFlow()

    companion object {
        private val SENSOR_SERVICE_UUID = UUID.fromString("fed40001-bbbb-d457-a78c-44ae22d29971")
        private val ECG_CHARACTERISTIC_UUID = UUID.fromString("fed40002-bbbb-d457-a78c-44ae22d29971")
        private val BAT_CHARACTERISTIC_UUID = UUID.fromString("fed40007-bbbb-d457-a78c-44ae22d29971")

    }

    override fun isRequiredServiceSupported(gatt: BluetoothGatt): Boolean {
        val sensorService = gatt.getService(SENSOR_SERVICE_UUID)
        sensorService?.let {
            ecgCharacteristic = it.getCharacteristic(ECG_CHARACTERISTIC_UUID)
            batteryCharacteristic=it.getCharacteristic(BAT_CHARACTERISTIC_UUID)
        }
        return ecgCharacteristic != null
    }

    override fun onDeviceReady() {
        super.onDeviceReady()
        _connectionState.value = BleConnectionState.Ready

        Log.d("BleDeviceManager", "Device is ready")
    }

    override fun onServicesInvalidated() {
        super.onServicesInvalidated()
        _connectionState.value = BleConnectionState.Invalidated
        Log.d("BleDeviceManager", "Services invalidated")
        ecgCharacteristic = null
        batteryCharacteristic = null
    }
      var deviceBle :BluetoothDevice?=null
    fun connectToDevice(device: BluetoothDevice) {
        deviceBle=device
        connect(device)
            .useAutoConnect(false)
            .timeout(10000)
            .retry(3, 100)
            .done {
                Log.d("CustomBleManager", "Device connected")
                _connectionState.value = BleConnectionState.Connected
            }
            .fail { _, status ->
                Log.e("CustomBleManager", "Connection failed: $status")
                _connectionState.value = BleConnectionState.Error
            }
            .enqueue()
    }
    override fun initialize() {
        super.initialize()
_connectionState.value = BleConnectionState.Connecting
        ecgCharacteristic?.let {
            setNotificationCallback(it).with { _, data ->
                data.value?.let { value ->
                    _ecgDataFlow.tryEmit(value)
                }
            }
            enableNotifications(it).enqueue()
        }

        batteryCharacteristic?.let {
            setNotificationCallback(it).with { _, data ->
                data.value?.let { value ->
                    _batteryData.postValue(value)
                }
            }
            readCharacteristic(it).enqueue()
            enableNotifications(it).enqueue()
        }
    }

    override fun onServerReady(server: BluetoothGattServer) {
        super.onServerReady(server)

    }


    fun disconnectDevice() {
        disconnect()
            .done {
                Log.d("CustomBleManager", "Device disconnected")
                _connectionState.value = BleConnectionState.Disconnected
            }
            .enqueue()
    }


    override fun close() {
        super.close()
        Log.d("CustomBleManager", "GATT Server closed.")

    }
    sealed class BleConnectionState {
        object Disconnected : BleConnectionState()
        object Connecting : BleConnectionState()
        object Connected : BleConnectionState()
        object Ready : BleConnectionState()
        object Error:BleConnectionState()
        object Invalidated : BleConnectionState()
    }
}
