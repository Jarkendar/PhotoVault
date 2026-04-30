package dev.jarkendar.photovault.core.data.mapper.database

import dev.jarkendar.photovault.core.database.entity.CategoryEntity
import dev.jarkendar.photovault.core.domain.id.CategoryId
import dev.jarkendar.photovault.core.domain.model.Category

internal fun CategoryEntity.toDomain(): Category = Category(
    id = CategoryId(id),
    name = name,
    colorHex = colorHex,
)

internal fun Category.toEntity(): CategoryEntity = CategoryEntity(
    id = id.value,
    name = name,
    colorHex = colorHex,
)