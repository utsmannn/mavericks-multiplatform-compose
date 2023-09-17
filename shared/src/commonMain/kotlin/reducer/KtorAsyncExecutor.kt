package reducer

import com.utsman.mavericks.core.common.ApiException
import com.utsman.mavericks.core.common.Async
import com.utsman.mavericks.core.common.AsyncExecutor
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.RedirectResponseException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart

class KtorAsyncExecutor<T: HttpResponse> : AsyncExecutor<T> {
    override suspend fun reduce(execute: suspend () -> T): Flow<Async<T>> {
        return flow {
            val httpResponse = execute.invoke()
            if (httpResponse.status == HttpStatusCode.OK) {
                emit(Async.Success(httpResponse))
            } else {
                emit(Async.Failure(
                    ApiException(httpResponse.status.value, httpResponse.bodyAsText())
                ))
            }
        }.catch {
            when (it) {
                is ClientRequestException -> {
                    val apiException = ApiException(it.response.status.value, it.response.bodyAsText())
                    emit(Async.Failure(apiException))
                }
                is RedirectResponseException -> {
                    val apiException = ApiException(it.response.status.value, it.response.bodyAsText())
                    emit(Async.Failure(apiException))
                }
                is ServerResponseException -> {
                    val apiException = ApiException(it.response.status.value, it.response.bodyAsText())
                    emit(Async.Failure(apiException))
                }
                else -> {
                    emit(Async.Failure(it))
                }
            }
        }.onStart {
            emit(Async.Loading)
        }
    }
}