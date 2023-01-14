package com.bartoszdrozd.fitapp.domain

import android.util.Log
import com.bartoszdrozd.fitapp.utils.ResultValue
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn

abstract class FlowUseCase<in P, R>(private val coroutineDispatcher: CoroutineDispatcher) {
    suspend operator fun invoke(params: P): Flow<ResultValue<R>> = execute(params)
        .catch { e ->
            emit(ResultValue.Error(Exception(e)))
        }
        .flowOn(coroutineDispatcher)

    protected abstract suspend fun execute(params: P): Flow<ResultValue<R>>
}