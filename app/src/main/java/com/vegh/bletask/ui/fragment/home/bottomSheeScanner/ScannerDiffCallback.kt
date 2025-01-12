package com.fitroad.android.ui.home.bottomSheeScanner

import android.bluetooth.le.ScanResult
import androidx.recyclerview.widget.DiffUtil

class ScannerDiffCallback(
    private val oldList: MutableList<no.nordicsemi.android.support.v18.scanner.ScanResult>,
    private val newList: List<no.nordicsemi.android.support.v18.scanner.ScanResult>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].device == newList[newItemPosition].device
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}