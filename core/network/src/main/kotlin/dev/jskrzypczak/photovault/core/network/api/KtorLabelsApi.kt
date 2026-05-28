package dev.jarkendar.photovault.core.network.api

import dev.jarkendar.photovault.core.network.dto.label.LabelListDto
import dev.jarkendar.photovault.core.network.error.mapToNetworkError
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.isSuccess

class KtorLabelsApi(private val client: HttpClient) : LabelsApi {
    override suspend fun listLabels(): Result<LabelListDto> = runCatching {
        val response = client.get("labels")
        if (!response.status.isSuccess()) throw mapToNetworkError(response)
        response.body<LabelListDto>()
    }.recoverCatching { throw mapToNetworkError(it) }
}