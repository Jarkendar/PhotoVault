package dev.jarkendar.photovault.core.network.dto.photo

import kotlinx.serialization.Serializable

@Serializable
data class PhotoPatchRequestDto(
    val isFavorite: Boolean? = null,
    val tagIds: List<String>? = null,
    val categoryIds: List<String>? = null,
    val labelIds: List<String>? = null,
)