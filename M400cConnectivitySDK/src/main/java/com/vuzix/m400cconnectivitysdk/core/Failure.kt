package com.vuzix.m400cconnectivitysdk.core

sealed class Failure {
    // A failure related to the feature itself.
    abstract class FeatureFailure : Failure()

    // A failure due to the data being received for the feature.
    abstract class DataFailure: Failure()

    // A failure of unknown origin
    object UncaughtFailure : Failure()
}