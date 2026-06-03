package dev.jskrzypczak.photovault.core.network

import dev.jskrzypczak.photovault.core.network.auth.TokenStore
import dev.jskrzypczak.photovault.core.network.dto.auth.AuthResponseDto
import dev.jskrzypczak.photovault.core.network.dto.auth.RefreshRequestDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
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
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

private val publicEndpointSuffixes = setOf(
    "/auth/login",
    "/auth/refresh",
    "/health",
)

fun createPhotoVaultHttpClient(
    baseUrlProvider: BaseUrlProvider,
    tokenStore: TokenStore,
    enableLogging: Boolean = false,
    engine: HttpClientEngine? = null,
): HttpClient {
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
            val rawUrl = baseUrlProvider.current()
            val normalizedUrl = if (rawUrl.endsWith("/")) rawUrl else "$rawUrl/"
            url(normalizedUrl)
            header(HttpHeaders.ContentType, "application/json")
        }

        install(Auth) {
            bearer {
                loadTokens {
                    val access = tokenStore.accessToken() ?: return@loadTokens null
                    val refresh = tokenStore.refreshToken().orEmpty()
                    BearerTokens(access, refresh)
                }
                sendWithoutRequest { request ->
                    val path = "/" + request.url.encodedPathSegments.joinToString("/")
                    publicEndpointSuffixes.none { path.endsWith(it) }
                }
                refreshTokens {
                    // Called automatically by Ktor when a protected endpoint returns 401.
                    // markAsRefreshTokenRequest() prevents an infinite retry loop.
                    val refresh = tokenStore.refreshToken() ?: return@refreshTokens null
                    val response = client.post("auth/refresh") {
                        markAsRefreshTokenRequest()
                        contentType(ContentType.Application.Json)
                        setBody(RefreshRequestDto(refresh))
                    }
                    if (!response.status.isSuccess()) {
                        // Refresh token is invalid/expired — force the user to log in again.
                        tokenStore.clear()
                        return@refreshTokens null
                    }
                    val dto = response.body<AuthResponseDto>()
                    tokenStore.save(dto.accessToken, dto.refreshToken)
                    BearerTokens(dto.accessToken, dto.refreshToken)
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

/**
 * Test-friendly overload that accepts a constant [baseUrl] string instead of a [BaseUrlProvider].
 * Production code should use the [BaseUrlProvider] overload.
 */
fun createPhotoVaultHttpClient(
    baseUrl: String,
    tokenStore: TokenStore,
    enableLogging: Boolean = false,
    engine: HttpClientEngine? = null,
): HttpClient = createPhotoVaultHttpClient(
    baseUrlProvider = object : BaseUrlProvider {
        override fun current(): String = baseUrl
    },
    tokenStore = tokenStore,
    enableLogging = enableLogging,
    engine = engine,
)
