package dev.jskrzypczak.photovault.core.network.api

import dev.jskrzypczak.photovault.core.network.dto.upload.UploadDto
import dev.jskrzypczak.photovault.core.network.dto.upload.UploadListDto
import dev.jskrzypczak.photovault.core.network.dto.upload.UploadStatus

interface UploadsApi {
    suspend fun uploadPhoto(bytes: ByteArray, fileName: String, mimeType: String): Result<UploadDto>
    suspend fun listUploads(statuses: List<UploadStatus>? = null): Result<UploadListDto>
    suspend fun getUpload(id: String): Result<UploadDto>
    suspend fun deleteUpload(id: String): Result<Unit>
}