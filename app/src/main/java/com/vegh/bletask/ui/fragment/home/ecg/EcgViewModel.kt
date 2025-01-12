package com.vegh.bletask.ui.fragment.home.ecg

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vegh.bletask.utils.bleTools.BleDeviceManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EcgViewModel @Inject constructor(private val bleManager: BleDeviceManager) : ViewModel() {
    private val _ecgData = MutableStateFlow(byteArrayOf())
    val ecgData: StateFlow<ByteArray> get() = _ecgData

    init {
        collectEcgData()
    }

    private fun collectEcgData() {
        viewModelScope.launch {
            bleManager.ecgDataFlow.collect { data ->
                _ecgData.value = data
            }
        }
    }
}