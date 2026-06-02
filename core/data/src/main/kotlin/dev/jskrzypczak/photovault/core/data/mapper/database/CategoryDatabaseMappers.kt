package dev.jskrzypczak.photovault.core.data.mapper.database

import dev.jskrzypczak.photovault.core.database.entity.CategoryEntity
import dev.jskrzypczak.photovault.core.domain.id.CategoryId
import dev.jskrzypczak.photovault.core.domain.model.Category

internal fun CategoryEntity.toDomain(): Category = Category(
    id = CategoryId(id),
    name = name,
    colorHex = colorHex,
    photoCount = photoCount,
)

internal fun Category.toEntity(): CategoryEntity = CategoryEntity(
    id = id.value,
    name = name,
    colorHex = colorHex,
    photoCount = photoCount,
)