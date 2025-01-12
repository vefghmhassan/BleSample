package com.vegh.bletask.utils.bleTools

object FilterBleItem {


    fun showAnimateCharge(data: ByteArray): Boolean = data[1].toInt() and 0x03 == 3
    fun batteryState(data: ByteArray): String {
        return String.format("%d%%", data[0])
    }

     fun stateBattery(data: ByteArray): String {
        val state = data[1].toInt()
        val batteryOnCharging = (state and 0x01)
        val batteryPowerGood = (state and 0x02)
        return when {
            batteryOnCharging == 1 -> "Charging"
            batteryPowerGood == 2 -> ""
            else -> {
                ""
            }
        }
    }
}