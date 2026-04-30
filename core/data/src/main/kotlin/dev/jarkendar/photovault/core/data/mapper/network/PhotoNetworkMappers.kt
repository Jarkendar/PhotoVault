package dev.jarkendar.photovault.core.data.mapper.network

import dev.jarkendar.photovault.core.domain.id.PhotoId
import dev.jarkendar.photovault.core.domain.model.GeoLocation
import dev.jarkendar.photovault.core.domain.model.Photo
import dev.jarkendar.photovault.core.network.dto.photo.LocationDto
import dev.jarkendar.photovault.core.network.dto.photo.PhotoDto
import dev.jarkendar.photovault.core.network.dto.photo.PhotoPageDto

internal fun PhotoDto.toDomain(): Photo = Photo(
    id = PhotoId(id),
    name = name,
    sizeBytes = sizeBytes,
    mimeType = mimeType,
    width = width,
    height = height,
    capturedAt = capturedAt,
    uploadedAt = uploadedAt,
    camera = camera,
    location = location?.toDomain(),
    tags = tags.map { it.toDomain() },
    categories = categories.map { it.toDomain() },
    labels = labels.map { it.toDomain() },
    isFavorite = isFavorite,
)

internal fun LocationDto.toDomain(): GeoLocation = GeoLocation(
    latitude = latitude,
    longitude = longitude,
    placeName = placeName,
)

internal fun PhotoPageDto.toDomain(): List<Photo> = items.map { it.toDomain() }