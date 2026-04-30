package dev.jarkendar.photovault.core.network.api

import dev.jarkendar.photovault.core.network.dto.health.HealthDto
import dev.jarkendar.photovault.core.network.error.mapToNetworkError
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.isSuccess

class KtorHealthApi(private val client: HttpClient) : HealthApi {
    override suspend fun getHealth(): Result<HealthDto> = runCatching {
        val response = client.get("health")
        if (!response.status.isSuccess()) throw mapToNetworkError(response)
        response.body<HealthDto>()
    }.recoverCatching { throw mapToNetworkError(it) }
}