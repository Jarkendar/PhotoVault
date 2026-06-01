package dev.jskrzypczak.photovault.core.network.api

import dev.jskrzypczak.photovault.core.network.dto.upload.UploadDto
import dev.jskrzypczak.photovault.core.network.dto.upload.UploadListDto
import dev.jskrzypczak.photovault.core.network.dto.upload.UploadStatus
import dev.jskrzypczak.photovault.core.network.error.mapToNetworkError
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.isSuccess

class KtorUploadsApi(private val client: HttpClient) : UploadsApi {

    override suspend fun uploadPhoto(
        bytes: ByteArray,
        fileName: String,
        mimeType: String,
    ): Result<UploadDto> = runCatching {
        val response = client.submitFormWithBinaryData(
            url = "uploads",
            formData = formData {
                append(
                    key = "file",
                    value = bytes,
                    headers = Headers.build {
                        append(HttpHeaders.ContentType, mimeType)
                        append(HttpHeaders.ContentDisposition, "filename=\"$fileName\"")
                    },
                )
            },
        )
        if (!response.status.isSuccess()) throw mapToNetworkError(response)
        response.body<UploadDto>()
    }.recoverCatching { throw mapToNetworkError(it) }

    override suspend fun listUploads(statuses: List<UploadStatus>?): Result<UploadListDto> = runCatching {
        val response = client.get("uploads") {
            statuses?.takeIf { it.isNotEmpty() }?.let {
                parameter("status", it.joinToString(",") { status -> status.serialName() })
            }
        }
        if (!response.status.isSuccess()) throw mapToNetworkError(response)
        response.body<UploadListDto>()
    }.recoverCatching { throw mapToNetworkError(it) }

    override suspend fun getUpload(id: String): Result<UploadDto> = runCatching {
        val response = client.get("uploads/$id")
        if (!response.status.isSuccess()) throw mapToNetworkError(response)
        response.body<UploadDto>()
    }.recoverCatching { throw mapToNetworkError(it) }

    override suspend fun deleteUpload(id: String): Result<Unit> = runCatching {
        val response = client.delete("uploads/$id")
        if (!response.status.isSuccess()) throw mapToNetworkError(response)
        Unit
    }.recoverCatching { throw mapToNetworkError(it) }
}

private fun UploadStatus.serialName(): String = when (this) {
    UploadStatus.CREATED -> "created"
    UploadStatus.UPLOADING -> "uploading"
    UploadStatus.PROCESSING -> "processing"
    UploadStatus.DONE -> "done"
    UploadStatus.FAILED -> "failed"
    UploadStatus.CANCELLED -> "cancelled"
}