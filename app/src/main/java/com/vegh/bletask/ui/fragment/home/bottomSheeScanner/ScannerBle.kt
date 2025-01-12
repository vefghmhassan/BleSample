package com.vegh.bletask.ui.fragment.home.bottomSheeScanner

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.vegh.bletask.MainActivity
import com.vegh.bletask.R
import com.vegh.bletask.databinding.BottomsheetScannerBleBinding

import com.vegh.bletask.utils.BaseBottomSheetFragment
import com.vegh.bletask.utils.CustomClick
import no.nordicsemi.android.support.v18.scanner.ScanResult

class ScannerBle : BaseBottomSheetFragment<BottomsheetScannerBleBinding>() {
    override fun layoutId() = R.layout.bottomsheet_scanner_ble
    private lateinit var viewModel: BottomSheetScannerViewModel
    lateinit var adapter: AdapterScanner
    private val scanResultList: MutableList<ScanResult> = mutableListOf()
    private val uniqueScanResults: MutableSet<ScanResult> = mutableSetOf()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rcScannerBle.layoutManager = LinearLayoutManager(requireContext())
        adapter = AdapterScanner()
        binding.rcScannerBle.adapter = adapter
        adapter.click = CustomClick {
            if (it != null) {
                //   connect to device ble
                MainActivity.self.deviceManager?.connectToDevice(it)
            }
            dismiss()
        }
        viewModel = ViewModelProvider(this)[BottomSheetScannerViewModel::class.java]
        checkPermission()
        viewModel.list.observe(viewLifecycleOwner) {


            it?.let {
                filterList(it)

                adapter.setListItem(scanResultList)

            }
        }

    }

    @SuppressLint("MissingPermission")
    private fun filterList(it: List<ScanResult>) {
        val items = it.filter { !it.scanRecord?.deviceName.isNullOrEmpty() }

        for (item in items) {
            val isDuplicate = scanResultList.any { existingItem ->
                existingItem.device.address == item.device.address
            }

            if (!isDuplicate) {
                scanResultList.add(item)
            }
        }
    }



    fun checkPermission() {
        val context = requireContext()

        val isBluetoothScanPermissionGranted = ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.BLUETOOTH_SCAN
        ) == PackageManager.PERMISSION_GRANTED

        val isBluetoothConnectPermissionGranted = ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.BLUETOOTH_CONNECT
        ) == PackageManager.PERMISSION_GRANTED

        val isLocationPermissionGranted = ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (isBluetoothScanPermissionGranted && isBluetoothConnectPermissionGranted && isLocationPermissionGranted) {
            viewModel.startScanner()
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(
                        Manifest.permission.BLUETOOTH_SCAN,
                        Manifest.permission.BLUETOOTH_CONNECT,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        // Add any additional permissions you need
                    ),
                    111
                )
            } else {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(
                        Manifest.permission.BLUETOOTH_SCAN,
                        Manifest.permission.BLUETOOTH_CONNECT,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ),
                    111
                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.stopScan()
    }
}