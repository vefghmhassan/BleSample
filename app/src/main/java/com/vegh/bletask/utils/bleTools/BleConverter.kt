package com.vegh.bletask.utils.bleTools


object BleConverter {
    fun convertR2R(bytes: ByteArray): String {
        var data = "...."
        data = if (bytes[0] != 0.toByte() || bytes[1] != 0.toByte()) {
            val r2rTempValue = (60000 / (((bytes[0].toInt() and 0x3F) shl 8) + (bytes[1].toInt() and 0xFF).toDouble() / 7.8125)).toLong()
            if (r2rTempValue > 30 && r2rTempValue < 250) {
                r2rTempValue.toString()
            } else {
                "..."
            }
        } else {
            "..."
        }
        return data
    }
}