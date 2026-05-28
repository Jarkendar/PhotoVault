package dev.jarkendar.photovault.core.domain.model

import dev.jarkendar.photovault.core.domain.id.CategoryId

data class Category(
    val id: CategoryId,
    val name: String,
    val colorHex: String,
)
