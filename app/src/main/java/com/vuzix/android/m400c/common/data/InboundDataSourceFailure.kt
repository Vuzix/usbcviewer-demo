package com.vuzix.android.m400c.common.data

import com.vuzix.android.m400c.core.util.Failure

data class InboundDataSourceFailure(val error: String) : Failure.DataFailure()