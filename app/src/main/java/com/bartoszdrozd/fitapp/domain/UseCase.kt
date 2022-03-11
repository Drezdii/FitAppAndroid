package com.bartoszdrozd.fitapp.domain

import android.util.Log
import com.bartoszdrozd.fitapp.utils.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

abstract class UseCase<in P, R>(private val coroutineDispatcher: CoroutineDispatcher) {
    suspend operator fun invoke(params: P): Result<R> {
        return try {
            withContext(coroutineDispatcher) {
                execute(params).let {
                    Result.Success(it)
                }
            }
        } catch (e: Exception) {
            Log.e("EXCEPTION", e.stackTraceToString())
            Result.Error(e)
        }
    }

    protected abstract suspend fun execute(params: P): R
}