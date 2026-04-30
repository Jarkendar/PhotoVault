package dev.jarkendar.photovault.core.network.dto.category

import kotlinx.serialization.Serializable

@Serializable
data class CategoryCreateRequestDto(
    val name: String,
    val colorHex: String,
)