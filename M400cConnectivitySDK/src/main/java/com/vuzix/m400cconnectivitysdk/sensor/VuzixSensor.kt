package com.vuzix.m400cconnectivitysdk.sensor

class VuzixSensor {

    private var mType : Int = 0;

    fun setType(value: Int) {
        mType = value
    }

    fun getType(): Int {
        return mType
    }
}