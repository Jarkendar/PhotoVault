package dev.jskrzypczak.photovault.core.network.dto.category

import kotlinx.serialization.Serializable

@Serializable
data class CategoryUpdateRequestDto(
    val name: String? = null,
    val colorHex: String? = null,
    val autoEnabled: Boolean? = null,
    val rolledOut: Boolean? = null,
)