package dev.jarkendar.photovault.core.network

import dev.jarkendar.photovault.core.network.auth.AuthTokenProvider
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

private val publicEndpointSuffixes = setOf(
    "/auth/login",
    "/auth/refresh",
    "/health",
)

fun createPhotoVaultHttpClient(
    baseUrl: String,
    tokenProvider: AuthTokenProvider,
    enableLogging: Boolean = false,
    engine: HttpClientEngine? = null,
): HttpClient {
    val normalizedBaseUrl = if (baseUrl.endsWith("/")) baseUrl else "$baseUrl/"
    val configure: io.ktor.client.HttpClientConfig<*>.() -> Unit = {
        expectSuccess = false

        install(ContentNegotiation) {
            json(
                Json {
                    ignoreUnknownKeys = true
                    explicitNulls = false
                    isLenient = true
                },
            )
        }

        install(DefaultRequest) {
            url(normalizedBaseUrl)
            header(HttpHeaders.ContentType, "application/json")
        }

        install(Auth) {
            bearer {
                loadTokens {
                    val access = tokenProvider.getAccessToken() ?: return@loadTokens null
                    val refresh = tokenProvider.getRefreshToken().orEmpty()
                    BearerTokens(access, refresh)
                }
                sendWithoutRequest { request ->
                    val path = "/" + request.url.encodedPathSegments.joinToString("/")
                    publicEndpointSuffixes.none { path.endsWith(it) }
                }
            }
        }

        if (enableLogging) {
            install(Logging) {
                level = LogLevel.HEADERS
            }
        }
    }
    return if (engine != null) HttpClient(engine, configure) else HttpClient(OkHttp, configure)
}
