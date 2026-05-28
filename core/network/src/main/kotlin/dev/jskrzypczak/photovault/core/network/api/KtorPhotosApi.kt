package dev.jarkendar.photovault.core.network.api

import dev.jarkendar.photovault.core.network.dto.photo.PhotoDto
import dev.jarkendar.photovault.core.network.dto.photo.PhotoPageDto
import dev.jarkendar.photovault.core.network.dto.photo.PhotoPatchRequestDto
import dev.jarkendar.photovault.core.network.error.mapToNetworkError
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.patch
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess

class KtorPhotosApi(private val client: HttpClient) : PhotosApi {

    override suspend fun listPhotos(
        cursor: String?,
        limit: Int,
        q: String?,
        tagIds: List<String>?,
        categoryIds: List<String>?,
        labelIds: List<String>?,
        favoritesOnly: Boolean,
        uploadedBy: String?,
    ): Result<PhotoPageDto> = runCatching {
        val response = client.get("photos") {
            cursor?.let { parameter("cursor", it) }
            parameter("limit", limit)
            q?.let { parameter("q", it) }
            tagIds?.takeIf { it.isNotEmpty() }?.let { parameter("tagIds", it.joinToString(",")) }
            categoryIds?.takeIf { it.isNotEmpty() }?.let { parameter("categoryIds", it.joinToString(",")) }
            labelIds?.takeIf { it.isNotEmpty() }?.let { parameter("labelIds", it.joinToString(",")) }
            if (favoritesOnly) parameter("favoritesOnly", true)
            uploadedBy?.let { parameter("uploadedBy", it) }
        }
        if (!response.status.isSuccess()) throw mapToNetworkError(response)
        response.body<PhotoPageDto>()
    }.recoverCatching { throw mapToNetworkError(it) }

    override suspend fun getPhoto(id: String): Result<PhotoDto> = runCatching {
        val response = client.get("photos/$id")
        if (!response.status.isSuccess()) throw mapToNetworkError(response)
        response.body<PhotoDto>()
    }.recoverCatching { throw mapToNetworkError(it) }

    override suspend fun patchPhoto(id: String, request: PhotoPatchRequestDto): Result<PhotoDto> = runCatching {
        val response = client.patch("photos/$id") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
        if (!response.status.isSuccess()) throw mapToNetworkError(response)
        response.body<PhotoDto>()
    }.recoverCatching { throw mapToNetworkError(it) }

    override suspend fun deletePhoto(id: String): Result<Unit> = runCatching {
        val response = client.delete("photos/$id")
        if (!response.status.isSuccess()) throw mapToNetworkError(response)
        Unit
    }.recoverCatching { throw mapToNetworkError(it) }
}
