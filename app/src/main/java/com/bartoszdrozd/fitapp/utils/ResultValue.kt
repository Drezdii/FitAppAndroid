package com.bartoszdrozd.fitapp.utils

sealed class ResultValue<out T> {
    data class Success<out T>(val data: T) : ResultValue<T>()
    data class Error(val exception: Exception) : ResultValue<Nothing>()
    object Loading : ResultValue<Nothing>()
}

val ResultValue<*>.succeeded
    get() = this is ResultValue.Success

val <T> ResultValue<T>.data: T?
    get() = (this as? ResultValue.Success)?.data