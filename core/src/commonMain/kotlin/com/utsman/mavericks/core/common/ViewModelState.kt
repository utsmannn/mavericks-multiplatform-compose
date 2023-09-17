package com.utsman.mavericks.core.common

import com.utsman.mavericks.core.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

abstract class ViewModelState<STATE : State, INTENT : Intent>(
    initialState: STATE
) : ViewModel() {

    private val _state: MutableStateFlow<STATE> = MutableStateFlow(initialState)
    val state: StateFlow<STATE> get() = _state

    abstract fun intent(intent: INTENT)

    protected fun setState(block: STATE.() -> STATE) = viewModelScope.launch {
        val currentState = _state.value
        val newState = block.invoke(currentState)
        _state.tryEmit(newState)
    }
}