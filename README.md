# Mavericks Style Architecture for Compose Kotlin Multiplatform

This is a repository of implementation Mavericks Style Architecture in the Kotlin Multiplatform with Compose for Multiplatform.

See full guild on medium:
https://medium.com/gravel-engineering/mavericks-style-architecture-on-kotlin-compose-multiplatform-a-tutorial-1903844812d3

## Easy implementation

### Set state ui

```kotlin
// state
data class CounterState(
    val counter: Int = 0
) : State

// intent
sealed class CounterIntent : Intent {
    object Increment : CounterIntent()
    object Decrement : CounterIntent()
}

// viewmodel
class CounterViewModel : ViewModelState<CounterState, CounterIntent>(CounterState()) {
    override fun intent(intent: CounterIntent) {
        when (intent) {
            is CounterIntent.Increment -> increment()
            is CounterIntent.Decrement -> decrement()
        }
    }

    private fun increment() = setState {
        copy(counter = this.counter + 1)
    }

    private fun decrement() = setState {
        copy(counter = this.counter - 1)
    }
}

// compose node
@Composable
fun Counter() {
    val counterViewModel = rememberViewModel { CounterViewModel() }
    val counterState by counterViewModel.state.collectAsState()

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        // error handling
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
        }
    }
}
```

### Asynchronous implementation to state ui
```kotlin
// state
data class CounterState(
    val asyncProductSize: Async<Int> = Async.Default
) : State

// intent
sealed class CounterIntent : Intent {
    object GetProductSize : CounterIntent()
}

// viewmodel
class CounterViewModel(
    private val productRepository: ProductRepository
) : ViewModelState<CounterState, CounterIntent>(CounterState()) {
    override fun intent(intent: CounterIntent) {
        when (intent) {
            is CounterIntent.GetProductSize -> getProductSize()
        }
    }

    private fun getProductSize() = viewModelScope.launch {
        suspend {
            productRepository.getProductResponse()
        }.execute(
            asyncExecutor = KtorAsyncExecutor()
        ) { response ->
            val asyncProductSize = response.map {
                it.body<ProductResponse>().data?.data?.size ?: 0
            }
            copy(asyncProductSize = asyncProductSize)
        }
    }
}

// compose node
@Composable
fun Counter() {
    val productRepository = remember { ProductRepository() }
    val counterViewModel = rememberViewModel { CounterViewModel(productRepository) }

    val counterState by counterViewModel.state.collectAsState()

    Scaffold {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            
            // ui handling
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
```

---