import com.utsman.mavericks.core.common.Intent

sealed class CounterIntent : Intent {
    object Increment : CounterIntent()
    object Decrement : CounterIntent()
}