package com.utsman.mavericks.core.viewmodel

import androidx.compose.runtime.Composable
import kotlinx.coroutines.CoroutineScope

expect abstract class ViewModel() {
    val viewModelScope: CoroutineScope

    protected open fun onCleared()
    fun clear()
}

@Composable
expect fun <T: ViewModel> rememberViewModel(viewModel: () -> T): T