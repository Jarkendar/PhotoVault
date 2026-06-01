package dev.jskrzypczak.photovault.core.data.mapper.network

import dev.jskrzypczak.photovault.core.domain.model.UploadJob
import dev.jskrzypczak.photovault.core.domain.model.UploadJobStatus
import dev.jskrzypczak.photovault.core.network.dto.upload.UploadDto
import dev.jskrzypczak.photovault.core.network.dto.upload.UploadStatus

fun UploadDto.toDomain(): UploadJob = UploadJob(
    id = id,
    fileName = fileName,
    sizeBytes = sizeBytes,
    uploadedBytes = uploadedBytes,
    status = status.toDomain(),
    progress = progress,
    photoId = photoId,
    error = error,
)

fun UploadStatus.toDomain(): UploadJobStatus = when (this) {
    UploadStatus.CREATED -> UploadJobStatus.CREATED
    UploadStatus.UPLOADING -> UploadJobStatus.UPLOADING
    UploadStatus.PROCESSING -> UploadJobStatus.PROCESSING
    UploadStatus.DONE -> UploadJobStatus.DONE
    UploadStatus.FAILED -> UploadJobStatus.FAILED
    UploadStatus.CANCELLED -> UploadJobStatus.CANCELLED
}

fun UploadJobStatus.toDto(): UploadStatus = when (this) {
    UploadJobStatus.CREATED -> UploadStatus.CREATED
    UploadJobStatus.UPLOADING -> UploadStatus.UPLOADING
    UploadJobStatus.PROCESSING -> UploadStatus.PROCESSING
    UploadJobStatus.DONE -> UploadStatus.DONE
    UploadJobStatus.FAILED -> UploadStatus.FAILED
    UploadJobStatus.CANCELLED -> UploadStatus.CANCELLED
}
