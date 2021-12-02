package com.vuzix.android.m400c.core.util

sealed class Either<out F, out S> {
    data class Failure<out F>(val failure: F) : Either<F, Nothing>()
    data class Success<out S>(val success: S) : Either<Nothing, S>()

    val isFailure: Boolean get() = this is Failure<F>
    val isSuccess: Boolean get() = this is Success<S>

    fun getOrNull(): S? =
        when (this) {
            is Failure -> null
            is Success -> success
        }

    fun leftOrNull(): F? =
        when (this) {
            is Failure -> failure
            is Success -> null
        }

    inline fun <C> handle(onFailure: (F) -> C, onSuccess: (S) -> C): C =
        when (this) {
            is Failure -> onFailure(failure)
            is Success -> onSuccess(success)
        }
}