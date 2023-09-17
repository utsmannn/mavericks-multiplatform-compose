package com.utsman.mavericks.core.common

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart

interface AsyncExecutor<T> {

    suspend fun reduce(execute: suspend () -> T): Flow<Async<T>>

    companion object {

        @Suppress("FunctionName")
        fun <T>Default(): AsyncExecutor<T> {
            return object : AsyncExecutor<T> {
                override suspend fun reduce(execute: suspend () -> T): Flow<Async<T>> {
                    return flow<Async<T>> {
                        val dataSuccess = execute.invoke()
                        emit(Async.Success(dataSuccess))
                    }.catch {
                        emit(Async.Failure(it))
                    }.onStart {
                        emit(Async.Loading)
                    }
                }
            }
        }
    }
}