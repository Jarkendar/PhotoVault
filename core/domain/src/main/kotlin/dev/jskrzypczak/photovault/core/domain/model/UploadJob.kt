package dev.jskrzypczak.photovault.core.domain.model

data class UploadJob(
    val id: String,
    val fileName: String,
    val sizeBytes: Long,
    val uploadedBytes: Long,
    val status: UploadJobStatus,
    val progress: Double,
    val photoId: String? = null,
    val error: String? = null,
)
