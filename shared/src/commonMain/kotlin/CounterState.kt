import com.utsman.mavericks.core.common.Async
import com.utsman.mavericks.core.common.State

data class CounterState(
    val counter: Int = 0,
    val asyncProductSize: Async<Int> = Async.Default
) : State