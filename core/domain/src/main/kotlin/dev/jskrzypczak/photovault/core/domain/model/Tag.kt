package dev.jskrzypczak.photovault.core.domain.model

import dev.jskrzypczak.photovault.core.domain.id.TagId

data class Tag(
    val id: TagId,
    val name: String,
)
