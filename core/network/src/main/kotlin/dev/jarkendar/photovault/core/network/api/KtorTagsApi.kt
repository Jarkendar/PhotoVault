package dev.jarkendar.photovault.core.network.api

import dev.jarkendar.photovault.core.network.dto.tag.TagCreateRequestDto
import dev.jarkendar.photovault.core.network.dto.tag.TagDto
import dev.jarkendar.photovault.core.network.dto.tag.TagListDto
import dev.jarkendar.photovault.core.network.dto.tag.TagUpdateRequestDto
import dev.jarkendar.photovault.core.network.error.mapToNetworkError
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess

class KtorTagsApi(private val client: HttpClient) : TagsApi {

    override suspend fun listTags(usedOnly: Boolean): Result<TagListDto> = runCatching {
        val response = client.get("tags") {
            if (usedOnly) parameter("usedOnly", true)
        }
        if (!response.status.isSuccess()) throw mapToNetworkError(response)
        response.body<TagListDto>()
    }.recoverCatching { throw mapToNetworkError(it) }

    override suspend fun createTag(request: TagCreateRequestDto): Result<TagDto> = runCatching {
        val response = client.post("tags") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
        if (!response.status.isSuccess()) throw mapToNetworkError(response)
        response.body<TagDto>()
    }.recoverCatching { throw mapToNetworkError(it) }

    override suspend fun patchTag(id: String, request: TagUpdateRequestDto): Result<TagDto> = runCatching {
        val response = client.patch("tags/$id") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
        if (!response.status.isSuccess()) throw mapToNetworkError(response)
        response.body<TagDto>()
    }.recoverCatching { throw mapToNetworkError(it) }

    override suspend fun deleteTag(id: String): Result<Unit> = runCatching {
        val response = client.delete("tags/$id")
        if (!response.status.isSuccess()) throw mapToNetworkError(response)
        Unit
    }.recoverCatching { throw mapToNetworkError(it) }
}
