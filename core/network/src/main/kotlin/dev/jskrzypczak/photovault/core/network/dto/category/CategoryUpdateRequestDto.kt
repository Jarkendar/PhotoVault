package dev.jarkendar.photovault.core.network.dto.category

import kotlinx.serialization.Serializable

@Serializable
data class CategoryUpdateRequestDto(
    val name: String? = null,
    val colorHex: String? = null,
)