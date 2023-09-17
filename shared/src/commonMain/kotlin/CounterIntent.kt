import com.utsman.mavericks.core.common.Intent

sealed class CounterIntent : Intent {
    object Increment : CounterIntent()
    object Decrement : CounterIntent()
    data class Divide(val value: Int) : CounterIntent()
    object GetProductSize : CounterIntent()
}