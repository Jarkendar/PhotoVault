package dev.jskrzypczak.photovault.core.domain.repository

import dev.jskrzypczak.photovault.core.domain.model.UploadJob
import dev.jskrzypczak.photovault.core.domain.model.UploadJobStatus

interface UploadRepository {
    suspend fun uploadPhoto(bytes: ByteArray, fileName: String, mimeType: String): Result<UploadJob>
    suspend fun getUploadStatus(id: String): Result<UploadJob>
    suspend fun cancelUpload(id: String): Result<Unit>
    suspend fun listUploads(statuses: List<UploadJobStatus>? = null): Result<List<UploadJob>>
}
