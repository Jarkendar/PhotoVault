package dev.jarkendar.photovault.core.network.api

import dev.jarkendar.photovault.core.network.dto.auth.AuthResponseDto
import dev.jarkendar.photovault.core.network.dto.auth.LoginRequestDto
import dev.jarkendar.photovault.core.network.dto.auth.RefreshRequestDto
import dev.jarkendar.photovault.core.network.dto.user.UserDto
import dev.jarkendar.photovault.core.network.error.mapToNetworkError
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess

class KtorAuthApi(private val client: HttpClient) : AuthApi {

    override suspend fun login(request: LoginRequestDto): Result<AuthResponseDto> = runCatching {
        val response = client.post("auth/login") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
        if (!response.status.isSuccess()) throw mapToNetworkError(response)
        response.body<AuthResponseDto>()
    }.recoverCatching { throw mapToNetworkError(it) }

    override suspend fun refresh(request: RefreshRequestDto): Result<AuthResponseDto> = runCatching {
        val response = client.post("auth/refresh") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
        if (!response.status.isSuccess()) throw mapToNetworkError(response)
        response.body<AuthResponseDto>()
    }.recoverCatching { throw mapToNetworkError(it) }

    override suspend fun logout(): Result<Unit> = runCatching {
        val response = client.post("auth/logout")
        if (!response.status.isSuccess()) throw mapToNetworkError(response)
        Unit
    }.recoverCatching { throw mapToNetworkError(it) }

    override suspend fun me(): Result<UserDto> = runCatching {
        val response = client.get("auth/me")
        if (!response.status.isSuccess()) throw mapToNetworkError(response)
        response.body<UserDto>()
    }.recoverCatching { throw mapToNetworkError(it) }
}
