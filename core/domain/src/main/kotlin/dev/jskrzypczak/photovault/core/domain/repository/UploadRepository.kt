package dev.jskrzypczak.photovault.core.domain.repository

import dev.jskrzypczak.photovault.core.domain.model.UploadFileKey
import dev.jskrzypczak.photovault.core.domain.model.UploadJob
import dev.jskrzypczak.photovault.core.domain.model.UploadJobStatus

interface UploadRepository {
    suspend fun uploadPhoto(bytes: ByteArray, fileName: String, mimeType: String): Result<UploadJob>
    suspend fun getUploadStatus(id: String): Result<UploadJob>
    suspend fun cancelUpload(id: String): Result<Unit>
    suspend fun listUploads(statuses: List<UploadJobStatus>? = null): Result<List<UploadJob>>

    /**
     * Returns the subset of [keys] that were already uploaded from this device
     * and recorded in the local ledger.
     */
    suspend fun findAlreadyUploaded(keys: List<UploadFileKey>): Set<UploadFileKey>

    /**
     * Persists [fileName] + [sizeBytes] in the local upload ledger so that future
     * calls to [findAlreadyUploaded] will recognise this file as a duplicate.
     */
    suspend fun rememberUploaded(fileName: String, sizeBytes: Long)
}
