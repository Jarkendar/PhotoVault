package dev.jskrzypczak.photovault.core.domain.model

import dev.jskrzypczak.photovault.core.domain.id.PhotoId
import kotlin.time.Instant
import kotlinx.collections.immutable.ImmutableList

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
    val tags: ImmutableList<Tag>,
    val categories: ImmutableList<Category>,
    val labels: ImmutableList<Label>,
    val isFavorite: Boolean,
    val processingStatus: ProcessingStatus,
    /** Absolute URL for the small thumbnail used in grid tiles. */
    val thumbnailUrl: String,
    /** Absolute URL for the medium-resolution version used in detail view. */
    val mediumUrl: String,
    /** Absolute URL for the original full-resolution file. */
    val originalUrl: String,
)
