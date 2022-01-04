package com.vuzix.android.m400c.core.util

import android.hardware.usb.UsbEndpoint

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

fun UsbEndpoint.allData(): String {
    val sb = StringBuilder()
    sb.append("Endpoint Values\n")
    sb.append("Content Description: ${this.describeContents()}\n")
    sb.append("Address: ${this.address}\n")
    sb.append("Attributes: ${this.attributes}\n")
    sb.append("Direction: ${this.direction}\n")
    sb.append("Endpoint Number: ${this.endpointNumber}\n")
    sb.append("Interval: ${this.interval}\n")
    sb.append("Max Packet Size: ${this.maxPacketSize}\n")
    sb.append("Type: ${this.type}")
    return sb.toString()
}