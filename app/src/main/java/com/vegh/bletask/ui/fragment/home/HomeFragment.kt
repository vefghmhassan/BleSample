package com.vegh.bletask.ui.fragment.home

import android.Manifest
import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asFlow
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.permissionx.guolindev.PermissionX

import com.vegh.bletask.MainActivity
import com.vegh.bletask.R
import com.vegh.bletask.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private lateinit var viewModel: HomeViewModel
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<Array<String>>
    lateinit var binding: FragmentHomeBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentHomeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            val isBluetoothScanGranted = permissions[Manifest.permission.BLUETOOTH_SCAN] == true
            val isLocationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true

            if (isBluetoothScanGranted && isLocationGranted) {
                checkConnectDevice()
                findNavController().navigate(R.id.action_global_scannerBle)

            } else {
            }
        }
    }

    private fun checkConnectDevice() {
        if (checkConnected() == true) {
            MainActivity.self.deviceManager?.disconnect()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        initClick()
        checkStateConnection()
        showBatteryLevel()
        showName()
    }


    private fun showBatteryLevel() {

        lifecycleScope.launch {
            viewModel.batteryLevel.asFlow().collect { batteryLevel ->
                if (batteryLevel != null) {
                    binding.nameBattery.text = "Battery: $batteryLevel%"
                } else {
                    binding.nameBattery.text = "Battery: --%"
                }
            }
        }
    }
    private fun showName() {
        lifecycleScope.launch {
            viewModel.deviceName.collect { name ->
                binding.title.text = viewModel.deviceName.value

            }
        }
    }

    private fun checkStateConnection() {

            lifecycleScope.launch {
                viewModel.connectionState.collect { state ->
                    requireActivity().runOnUiThread {
                        binding.textConnectBle.text = viewModel.getStateDescription(state)
                    }
                }
            }

    }


    private fun checkConnected() = MainActivity.self.deviceManager?.isConnected

    private fun initClick() {
        binding.bleConnector.setOnClickListener {
            checkPermissionNearby()
        }
        binding.layoutBattery.setOnClickListener {
            checkPermissionNearby()
        }
        binding.layoutEcg.setOnClickListener {
            if (checkConnected() == true) {
                findNavController().navigate(R.id.action_homeFragment_to_ecgFragment)
            } else {
                sendMessage()
            }
        }


    }

    private fun sendMessage() {
        Toast.makeText(requireContext(), "Device isn't connect", Toast.LENGTH_SHORT).show()
    }






    @SuppressLint("InlinedApi")
    fun checkPermissionNearby(){
        PermissionX.init(MainActivity.self)
            .permissions(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.BLUETOOTH_SCAN)
            .onExplainRequestReason { scope, deniedList ->
                scope.showRequestReasonDialog(deniedList, "Core fundamental are based on these permissions", "OK", "Cancel")
            }
            .request { allGranted, grantedList, deniedList ->
                if (allGranted) {
                    MainActivity.self.deviceManager.disconnectDevice()
                    findNavController().navigate(R.id.action_global_scannerBle)
                } else {

                   Toast.makeText(MainActivity.self, "These permissions are denied: $deniedList", Toast.LENGTH_LONG).show()
                }
            }
    }



    override fun onDestroy() {
//        RxBus.get().unregister(this)
        super.onDestroy()
    }


}