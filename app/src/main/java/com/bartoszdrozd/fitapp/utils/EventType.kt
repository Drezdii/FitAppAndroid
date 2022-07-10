package com.bartoszdrozd.fitapp.utils

//sealed class EventType {
//    object Success : Result<Nothing>()
//    data class Error(val exception: Exception) : Result<Nothing>()
//    object Loading : Result<Nothing>()
//}

sealed class EventType<out T> {
    object Created : EventType<Nothing>()
    object Updated : EventType<Nothing>()
    object Deleted : EventType<Nothing>()
    object Saved : EventType<Nothing>()
    object Loading : EventType<Nothing>()
    data class Error(val error: Throwable) : EventType<Nothing>()
}