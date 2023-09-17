package com.utsman.mavericks.core.common

sealed class Async<out T> {
    object Default : Async<Nothing>()
    object Loading : Async<Nothing>()
    data class Success<T>(val data: T) : Async<T>()
    data class Failure(val throwable: Throwable) : Async<Nothing>()
}