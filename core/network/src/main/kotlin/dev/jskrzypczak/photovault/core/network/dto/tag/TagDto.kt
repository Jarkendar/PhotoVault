package dev.jarkendar.photovault.core.network.dto.tag

import kotlinx.serialization.Serializable

@Serializable
data class TagDto(
    val id: String,
    val name: String,
    val photoCount: Int,
)

@Serializable
data class TagListDto(
    val items: List<TagDto>,
)