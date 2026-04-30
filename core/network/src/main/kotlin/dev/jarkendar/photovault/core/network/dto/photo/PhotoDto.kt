package dev.jarkendar.photovault.core.network.dto.photo

import dev.jarkendar.photovault.core.network.dto.category.CategoryDto
import dev.jarkendar.photovault.core.network.dto.label.LabelDto
import dev.jarkendar.photovault.core.network.dto.tag.TagDto
import dev.jarkendar.photovault.core.network.dto.user.UserRefDto
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PhotoDto(
    val id: String,
    val name: String,
    val sizeBytes: Long,
    val mimeType: String,
    val width: Int,
    val height: Int,
    val capturedAt: Instant? = null,
    val uploadedAt: Instant,
    val camera: String? = null,
    val location: LocationDto? = null,
    val uploadedBy: UserRefDto,
    val tags: List<TagDto>,
    val categories: List<CategoryDto>,
    val labels: List<LabelDto>,
    val isFavorite: Boolean,
    val processingStatus: ProcessingStatus,
    val thumbnailUrl: String,
    val mediumUrl: String,
    val originalUrl: String,
)

@Serializable
data class PhotoPageDto(
    val items: List<PhotoDto>,
    val nextCursor: String? = null,
    val hasMore: Boolean,
)

@Serializable
data class LocationDto(
    val latitude: Double,
    val longitude: Double,
    val placeName: String? = null,
)

@Serializable
enum class ProcessingStatus {
    @SerialName("processing") PROCESSING,
    @SerialName("ready") READY,
}