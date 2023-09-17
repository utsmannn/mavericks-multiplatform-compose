import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.utsman.mavericks.core.viewmodel.rememberViewModel

@Composable
fun Counter() {
    val counterViewModel = rememberViewModel { CounterViewModel() }

    val counterState by counterViewModel.state.collectAsState()

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