package com.bartoszdrozd.fitapp.domain

import android.util.Log
import com.bartoszdrozd.fitapp.utils.ResultValue
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

abstract class UseCase<in P, R>(private val coroutineDispatcher: CoroutineDispatcher) {
    suspend operator fun invoke(params: P): ResultValue<R> {
        return try {
            withContext(coroutineDispatcher) {
                execute(params).let {
                    ResultValue.Success(it)
                }
            }
        } catch (e: Exception) {
            Log.e("USE_CASE_EXCEPTION", e.stackTraceToString())
            ResultValue.Error(e)
        }
    }

    protected abstract suspend fun execute(params: P): R
}