package com.vuzix.m400cconnectivitysdk.core

class VuzixSensor {

    private var mType : Int = 0;

    fun setType(value: Int) {
        mType = value
    }

    fun getType(): Int {
        return mType
    }
}