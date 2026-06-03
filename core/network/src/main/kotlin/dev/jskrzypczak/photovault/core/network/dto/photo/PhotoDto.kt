package dev.jskrzypczak.photovault.core.network.dto.photo

import dev.jskrzypczak.photovault.core.network.dto.category.CategoryDto
import dev.jskrzypczak.photovault.core.network.dto.label.LabelDto
import dev.jskrzypczak.photovault.core.network.dto.tag.TagDto
import dev.jskrzypczak.photovault.core.network.dto.user.UserRefDto
import kotlin.time.Instant
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
    // TODO: processingStatus is absorbed here only to prevent deserialization crashes;
    //  the domain Photo model does not expose it. Decide whether to surface it or drop it.
    val processingStatus: ProcessingStatus = ProcessingStatus.DONE,
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
    @SerialName("done") DONE,
    @SerialName("failed") FAILED,
}