package dev.jskrzypczak.photovault.core.network.dto.tag

import kotlinx.serialization.Serializable

@Serializable
data class TagUpdateRequestDto(
    val name: String,
)
