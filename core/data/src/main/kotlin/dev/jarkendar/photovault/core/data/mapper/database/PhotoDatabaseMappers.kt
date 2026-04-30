package dev.jarkendar.photovault.core.data.mapper.database

import dev.jarkendar.photovault.core.database.entity.PhotoEntity
import dev.jarkendar.photovault.core.database.relation.PhotoWithRelations
import dev.jarkendar.photovault.core.domain.id.PhotoId
import dev.jarkendar.photovault.core.domain.model.GeoLocation
import dev.jarkendar.photovault.core.domain.model.Photo

internal fun PhotoWithRelations.toDomain(): Photo = Photo(
    id = PhotoId(photo.id),
    name = photo.name,
    sizeBytes = photo.sizeBytes,
    mimeType = photo.mimeType,
    width = photo.width,
    height = photo.height,
    capturedAt = photo.capturedAt,
    uploadedAt = photo.uploadedAt,
    camera = photo.camera,
    location = photo.toGeoLocation(),
    tags = tags.map { it.toDomain() },
    categories = categories.map { it.toDomain() },
    labels = labels.map { it.toDomain() },
    isFavorite = photo.isFavorite,
)

internal fun Photo.toEntity(): PhotoEntity = PhotoEntity(
    id = id.value,
    name = name,
    sizeBytes = sizeBytes,
    mimeType = mimeType,
    width = width,
    height = height,
    capturedAt = capturedAt,
    uploadedAt = uploadedAt,
    camera = camera,
    latitude = location?.latitude,
    longitude = location?.longitude,
    placeName = location?.placeName,
    isFavorite = isFavorite,
)

private fun PhotoEntity.toGeoLocation(): GeoLocation? {
    val lat = latitude ?: return null
    val lng = longitude ?: return null
    return GeoLocation(latitude = lat, longitude = lng, placeName = placeName)
}