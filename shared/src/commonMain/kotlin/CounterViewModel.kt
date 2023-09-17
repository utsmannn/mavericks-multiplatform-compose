import com.utsman.mavericks.core.common.ViewModelState
import data.ProductRepository
import data.ProductResponse
import io.ktor.client.call.body
import kotlinx.coroutines.launch
import reducer.KtorAsyncExecutor

class CounterViewModel(
    private val productRepository: ProductRepository
) : ViewModelState<CounterState, CounterIntent>(CounterState()) {
    override fun intent(intent: CounterIntent) {
        when (intent) {
            is CounterIntent.Increment -> increment()
            is CounterIntent.Decrement -> decrement()
            is CounterIntent.Divide -> divide(intent.value)
            is CounterIntent.GetProductSize -> getProductSize()
        }
    }

    private fun increment() = setState {
        copy(counter = this.counter + 1)
    }

    private fun decrement() = setState {
        copy(counter = this.counter - 1)
    }

    private fun divide(value: Int) = setState {
        if (value == 0) throw ArithmeticException("Divide zero is invalid!")
        copy(counter = this.counter / value)
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