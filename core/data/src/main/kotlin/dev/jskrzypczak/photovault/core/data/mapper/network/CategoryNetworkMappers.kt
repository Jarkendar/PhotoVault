package dev.jskrzypczak.photovault.core.data.mapper.network

import dev.jskrzypczak.photovault.core.domain.id.CategoryId
import dev.jskrzypczak.photovault.core.domain.model.Category
import dev.jskrzypczak.photovault.core.network.dto.category.CategoryDto

internal fun CategoryDto.toDomain(): Category = Category(
    id = CategoryId(id),
    name = name,
    colorHex = colorHex,
    photoCount = photoCount,
    autoEnabled = autoEnabled,
    rolledOut = rolledOut,
)