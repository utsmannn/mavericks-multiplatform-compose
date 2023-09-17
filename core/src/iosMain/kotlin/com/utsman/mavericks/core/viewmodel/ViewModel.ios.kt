package com.utsman.mavericks.core.viewmodel

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlin.coroutines.CoroutineContext

actual abstract class ViewModel {
    actual val viewModelScope: CoroutineScope = object : CoroutineScope {
        override val coroutineContext: CoroutineContext
            get() = SupervisorJob() + Dispatchers.IO
    }

    protected actual open fun onCleared() {
        viewModelScope.cancel()
    }

    actual fun clear() {
        onCleared()
    }
}

@Composable
actual fun <T: ViewModel> rememberViewModel(viewModel: () -> T): T {
    val vm = remember { viewModel.invoke() }
    DisposableEffect(vm) {
        onDispose {
            vm.clear()
        }
    }

    return vm
}