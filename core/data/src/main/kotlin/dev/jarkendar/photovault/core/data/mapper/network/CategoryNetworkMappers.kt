package dev.jarkendar.photovault.core.data.mapper.network

import dev.jarkendar.photovault.core.domain.id.CategoryId
import dev.jarkendar.photovault.core.domain.model.Category
import dev.jarkendar.photovault.core.network.dto.category.CategoryDto

internal fun CategoryDto.toDomain(): Category = Category(
    id = CategoryId(id),
    name = name,
    colorHex = colorHex,
)