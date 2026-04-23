package dev.jarkendar.photovault.core.domain.model

import dev.jarkendar.photovault.core.domain.id.PhotoId
import kotlinx.datetime.Instant

data class Photo(
    val id: PhotoId,
    val name: String,
    val sizeBytes: Long,
    val mimeType: String,
    val width: Int,
    val height: Int,
    val capturedAt: Instant?,
    val uploadedAt: Instant,
    val camera: String?,
    val location: GeoLocation?,
    val tags: List<Tag>,
    val categories: List<Category>,
    val labels: List<Label>,
    val isFavorite: Boolean,
)
