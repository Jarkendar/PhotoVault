package dev.jskrzypczak.photovault.core.data.repository

import dev.jskrzypczak.photovault.core.data.mapper.network.toDomain
import dev.jskrzypczak.photovault.core.data.mapper.network.toDto
import dev.jskrzypczak.photovault.core.domain.model.UploadJob
import dev.jskrzypczak.photovault.core.domain.model.UploadJobStatus
import dev.jskrzypczak.photovault.core.domain.repository.UploadRepository
import dev.jskrzypczak.photovault.core.network.api.UploadsApi

class UploadRepositoryImpl(private val api: UploadsApi) : UploadRepository {

    override suspend fun uploadPhoto(
        bytes: ByteArray,
        fileName: String,
        mimeType: String,
    ): Result<UploadJob> = api.uploadPhoto(bytes, fileName, mimeType).map { it.toDomain() }

    override suspend fun getUploadStatus(id: String): Result<UploadJob> =
        api.getUpload(id).map { it.toDomain() }

    override suspend fun cancelUpload(id: String): Result<Unit> =
        api.deleteUpload(id)

    override suspend fun listUploads(statuses: List<UploadJobStatus>?): Result<List<UploadJob>> =
        api.listUploads(statuses?.map { it.toDto() }).map { dto -> dto.items.map { it.toDomain() } }
}
