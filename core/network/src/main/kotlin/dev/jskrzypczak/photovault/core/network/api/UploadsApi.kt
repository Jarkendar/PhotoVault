package dev.jarkendar.photovault.core.network.api

import dev.jarkendar.photovault.core.network.dto.upload.UploadDto
import dev.jarkendar.photovault.core.network.dto.upload.UploadListDto
import dev.jarkendar.photovault.core.network.dto.upload.UploadStatus

interface UploadsApi {
    suspend fun listUploads(statuses: List<UploadStatus>? = null): Result<UploadListDto>
    suspend fun getUpload(id: String): Result<UploadDto>
    suspend fun deleteUpload(id: String): Result<Unit>
}