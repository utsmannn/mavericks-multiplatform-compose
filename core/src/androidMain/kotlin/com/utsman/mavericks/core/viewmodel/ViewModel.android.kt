package com.utsman.mavericks.core.viewmodel

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.ViewModel as LifecycleViewModel
import androidx.lifecycle.viewModelScope as androidViewModelScope

actual abstract class ViewModel : LifecycleViewModel() {
    actual val viewModelScope = androidViewModelScope

    actual override fun onCleared() {
        super.onCleared()
    }

    actual fun clear() {
        onCleared()
    }
}

@Composable
actual fun <T: ViewModel> rememberViewModel(viewModel: () -> T): T {
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val vm = remember { viewModel.invoke() }
    DisposableEffect(lifecycle) {
        onDispose {
            vm.clear()
        }
    }

    return vm
}