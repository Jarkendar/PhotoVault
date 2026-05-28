package dev.jarkendar.photovault.core.network.dto.category

import kotlinx.serialization.Serializable

@Serializable
data class CategoryDto(
    val id: String,
    val name: String,
    val colorHex: String,
    val photoCount: Int,
)

@Serializable
data class CategoryListDto(
    val items: List<CategoryDto>,
)