package com.bartoszdrozd.fitapp.utils

sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val exception: Exception) : Result<Nothing>()
    object Loading : Result<Nothing>()
}

val Result<*>.succeeded
    get() = this is Result.Success

val <T> Result<T>.data: T?
    get() = (this as? Result.Success)?.data