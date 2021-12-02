package com.vuzix.android.m400c.core.base

import com.vuzix.android.m400c.core.util.Either
import com.vuzix.android.m400c.core.util.Failure
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.withContext

interface BaseUseCase {
    interface Async<out Success, in Params> where Success : Any {
        suspend operator fun invoke(params: Params): Either<Failure, Success> {
            return withContext(Dispatchers.IO) {
                try {
                    run(params)
                } catch (e: Exception) {
                    Either.Failure(Failure.UncaughtFailure)
                }
            }
        }

        suspend fun run(params: Params): Either<Failure, Success>
    }
}