package dev.jskrzypczak.photovault.core.network.dto.category

import kotlinx.serialization.Serializable

@Serializable
data class CategoryDto(
    val id: String,
    val name: String,
    val colorHex: String,
    val photoCount: Int,
    val autoEnabled: Boolean = false,
    val rolledOut: Boolean = true,
)

@Serializable
data class CategoryListDto(
    val items: List<CategoryDto>,
)