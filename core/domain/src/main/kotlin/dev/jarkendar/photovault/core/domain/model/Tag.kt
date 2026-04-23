package dev.jarkendar.photovault.core.domain.model

import dev.jarkendar.photovault.core.domain.id.TagId

data class Tag(
    val id: TagId,
    val name: String,
)
