package dev.jarkendar.photovault.core.network.dto.upload

import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UploadDto(
    val id: String,
    val fileName: String,
    val sizeBytes: Long,
    val uploadedBytes: Long,
    val status: UploadStatus,
    val progress: Double,
    val photoId: String? = null,
    val error: String? = null,
    val createdAt: Instant,
)

@Serializable
data class UploadListDto(
    val items: List<UploadDto>,
)

@Serializable
enum class UploadStatus {
    @SerialName("created") CREATED,
    @SerialName("uploading") UPLOADING,
    @SerialName("processing") PROCESSING,
    @SerialName("done") DONE,
    @SerialName("failed") FAILED,
    @SerialName("cancelled") CANCELLED,
}