package com.utsman.mavericks.core.common

import com.utsman.mavericks.core.viewmodel.ViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus

abstract class ViewModelState<STATE : State, INTENT : Intent>(
    initialState: STATE
) : ViewModel() {

    private val _state: MutableStateFlow<STATE> = MutableStateFlow(initialState)
    val state: StateFlow<STATE> get() = _state

    private val _throwableState: MutableStateFlow<Throwable?> = MutableStateFlow(null)

    private val errorHandling = CoroutineExceptionHandler { coroutineContext, throwable ->
        _throwableState.tryEmit(throwable)
    }

    private val safeScope = viewModelScope + errorHandling

    fun catch(block: (Throwable) -> Unit) = safeScope.launch {
        _throwableState
            .filterNotNull()
            .collectLatest { block.invoke(it) }
    }

    abstract fun intent(intent: INTENT)

    protected fun setState(block: STATE.() -> STATE) = safeScope.launch {
        val currentState = _state.value
        val newState = block.invoke(currentState)
        _state.tryEmit(newState)
    }

    suspend fun <T : Any?> (suspend () -> T).execute(
        asyncExecutor: AsyncExecutor<T> = AsyncExecutor.Default(),
        reducer: suspend STATE.(Async<T>) -> STATE
    ): Job {
        return safeScope.launch {
            val currentState = _state.value
            asyncExecutor.reduce { invoke() }
                .collectLatest {
                    _state.tryEmit(reducer.invoke(currentState, it))
                }
        }
    }

    protected suspend fun <T, U> Async<T>.map(block: suspend (T) -> U): Async<U> {
        return if (this is Async.Success) {
            Async.Success(block.invoke(data))
        } else {
            when (this) {
                is Async.Default -> Async.Default
                is Async.Loading -> Async.Loading
                is Async.Failure -> Async.Failure(throwable)
                else -> throw IllegalArgumentException("Mapper failed")
            }
        }
    }
}