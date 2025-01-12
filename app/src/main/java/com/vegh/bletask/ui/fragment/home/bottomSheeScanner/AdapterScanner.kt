package com.vegh.bletask.ui.fragment.home.bottomSheeScanner

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import androidx.recyclerview.widget.DiffUtil

import com.fitroad.android.ui.home.bottomSheeScanner.ScannerDiffCallback
import com.fitroad.android.utils.BaseHolder

import com.vegh.bletask.R
import com.vegh.bletask.databinding.ItemCardScannerBinding
import com.vegh.bletask.utils.AdapterCustom
import com.vegh.bletask.utils.CustomClick
import no.nordicsemi.android.support.v18.scanner.ScanResult

class AdapterScanner: AdapterCustom<ItemCardScannerBinding, ScanResult>(){
    lateinit var click: CustomClick<BluetoothDevice>
    override fun layoutId()= R.layout.item_card_scanner
    init {
        list= mutableListOf()
    }

    @SuppressLint("SuspiciousIndentation")
    override fun onBindHolder(holder: BaseHolder<ItemCardScannerBinding>, position: Int) {
      holder.binding.item= list!![position]
        holder.itemView.setOnClickListener{
            click.Click(list[position].device)

        }
    }
    fun setListItem(newList: List<ScanResult>) {
        val diffResult = DiffUtil.calculateDiff(ScannerDiffCallback(list!!, newList))
        list?.clear()
        list?.addAll(newList)
        diffResult.dispatchUpdatesTo(this)
    }
}