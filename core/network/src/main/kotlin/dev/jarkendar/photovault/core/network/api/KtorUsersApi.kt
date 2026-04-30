package dev.jarkendar.photovault.core.network.api

import dev.jarkendar.photovault.core.network.dto.user.UserListDto
import dev.jarkendar.photovault.core.network.error.mapToNetworkError
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.isSuccess

class KtorUsersApi(private val client: HttpClient) : UsersApi {
    override suspend fun listUsers(): Result<UserListDto> = runCatching {
        val response = client.get("users")
        if (!response.status.isSuccess()) throw mapToNetworkError(response)
        response.body<UserListDto>()
    }.recoverCatching { throw mapToNetworkError(it) }
}