package com.vegh.bletask.ui.fragment.home

import android.annotation.SuppressLint
import android.bluetooth.BluetoothGatt
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.vegh.bletask.utils.bleTools.BleDeviceManager
import com.vegh.bletask.utils.bleTools.BleDeviceManager.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow

import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val bleManager: BleDeviceManager) : ViewModel() {

    val connectionState: StateFlow<BleConnectionState> = bleManager.connectionState

    private val _deviceName = MutableStateFlow<String>("")
    val deviceName: StateFlow<String> = _deviceName

    private val _connectionState = MutableStateFlow<BleConnectionState>(
        BleConnectionState.Disconnected
    )

    @SuppressLint("MissingPermission")
    private fun collectDeviceName() {
        viewModelScope.launch {
            bleManager.deviceNameFlow.collect { name ->
                _deviceName.value = bleManager.deviceBle?.name ?: ""
            }
        }
    }

    private fun observeBleManagerState() {
        viewModelScope.launch {
            bleManager.connectionState.collect { state ->
                _connectionState.value = state
            }
        }
    }
    private val _batteryLevel = MediatorLiveData<Int>()
    val batteryLevel: LiveData<Int> get() = _batteryLevel

    init {
        collectDeviceName()
        observeBleManagerState()
        _batteryLevel.addSource(bleManager.batteryData) { data ->
            _batteryLevel.value = parseBatteryData(data)
        }

            viewModelScope.launch {
                bleManager.batteryData.asFlow().collect { data ->
                    val batteryLevel = data.firstOrNull()?.toInt() ?: -1
                    _batteryLevel.value = batteryLevel
                }

        }
    }

    private fun parseBatteryData(data: ByteArray): Int {
        return data.firstOrNull()?.toInt() ?: -1
    }


    fun disconnectDevice() {
        bleManager.disconnectDevice()
    }

    fun getStateDescription(state: BleConnectionState): String {
        return when (state) {
            is BleConnectionState.Disconnected -> "Disconnected"
            is BleConnectionState.Connecting -> "Connecting..."
            is BleConnectionState.Connected -> "Connected"
            is BleConnectionState.Ready -> "Ready"
            is BleConnectionState.Error -> "Error"
            is BleConnectionState.Invalidated -> "Invalidated"
            else -> "Unknown State"
        }
    }
}

