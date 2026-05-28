package dev.jskrzypczak.photovault.core.domain.model

import dev.jskrzypczak.photovault.core.domain.id.CategoryId

data class Category(
    val id: CategoryId,
    val name: String,
    val colorHex: String,
)
