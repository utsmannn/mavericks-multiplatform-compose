package data

import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json

class ProductRepository {
    @OptIn(ExperimentalSerializationApi::class)
    private val client: HttpClient by lazy {
        HttpClient {
            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                    explicitNulls = false
                })
            }
            install(Logging) {
                logger = Logger.SIMPLE
                level = LogLevel.BODY
            }
        }
    }

    suspend fun getProductResponse(): HttpResponse {
        return client.get(URL) {
            contentType(ContentType.Application.Json)
        }
    }

    companion object {
        private const val URL = "https://footballstore.fly.dev/api/product"
    }
}