package dev.jarkendar.photovault.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.datetime.Instant

@Entity(tableName = "photos")
data class PhotoEntity(
    @PrimaryKey val id: String,
    val name: String,
    val sizeBytes: Long,
    val mimeType: String,
    val width: Int,
    val height: Int,
    val capturedAt: Instant?,
    val uploadedAt: Instant,
    val camera: String?,
    val latitude: Double?,
    val longitude: Double?,
    val placeName: String?,
    val isFavorite: Boolean,
)