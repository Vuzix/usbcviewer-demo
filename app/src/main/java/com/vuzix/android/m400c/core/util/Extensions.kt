package com.vuzix.android.m400c.core.util

fun ByteArray.strPrint(): String {
    val hexChars = CharArray(this.size * 2)
    val hexArray = "0123456789ABCDEF".toCharArray()
    for (i in this.indices) {
        val v = this[i].toInt() and 0xFF
        hexChars[i * 2] = hexArray[v ushr 4]
        hexChars[i * 2 + 1] = hexArray[v and 0x0F]
    }
    return hexChars.print()
}

fun CharArray.print(): String {
    val sb = StringBuilder()
    this.forEachIndexed { index, _ ->
        sb.append(this[index])
    }
    return sb.toString()
}