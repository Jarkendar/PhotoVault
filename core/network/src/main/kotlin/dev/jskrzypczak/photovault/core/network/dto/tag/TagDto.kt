package dev.jskrzypczak.photovault.core.network.dto.tag

import kotlinx.serialization.Serializable

@Serializable
data class TagDto(
    val id: String,
    val name: String,
    val photoCount: Int,
    val autoEnabled: Boolean = false,
    val rolledOut: Boolean = true,
)

@Serializable
data class TagListDto(
    val items: List<TagDto>,
)