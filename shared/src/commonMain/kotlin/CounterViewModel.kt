import com.utsman.mavericks.core.common.ViewModelState

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