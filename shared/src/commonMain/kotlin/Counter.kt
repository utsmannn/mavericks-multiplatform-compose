import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.utsman.mavericks.core.common.Async
import com.utsman.mavericks.core.viewmodel.rememberViewModel
import data.ProductRepository
import kotlinx.coroutines.launch

@Composable
fun Counter() {
    val productRepository = remember { ProductRepository() }
    val counterViewModel = rememberViewModel { CounterViewModel(productRepository) }

    val counterState by counterViewModel.state.collectAsState()

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        counterViewModel.catch {
            scope.launch {
                snackbarHostState.showSnackbar(it.message.orEmpty())
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(text = counterState.counter.toString())

            Button(
                onClick = {
                    counterViewModel.intent(CounterIntent.Decrement)
                }
            ) {
                Text("Decrement")
            }

            Button(
                onClick = {
                    counterViewModel.intent(CounterIntent.Increment)
                }
            ) {
                Text("Increment")
            }

            Button(
                onClick = {
                    counterViewModel.intent(CounterIntent.Divide(0))
                }
            ) {
                Text("Divide zero")
            }

            with(counterState.asyncProductSize) {
                when (this) {
                    is Async.Loading -> {
                        CircularProgressIndicator()
                    }
                    is Async.Failure -> {
                        Text("Product count failure: ${throwable.message}")
                    }
                    is Async.Success -> {
                        Text("Product count: $data")
                    }
                    is Async.Default -> {}
                }
            }

            Button(
                onClick = {
                    counterViewModel.intent(CounterIntent.GetProductSize)
                }
            ) {
                Text("Get product size")
            }
        }
    }
}