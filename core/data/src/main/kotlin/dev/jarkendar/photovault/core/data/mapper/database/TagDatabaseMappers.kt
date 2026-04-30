package dev.jarkendar.photovault.core.data.mapper.database

import dev.jarkendar.photovault.core.database.entity.TagEntity
import dev.jarkendar.photovault.core.domain.id.TagId
import dev.jarkendar.photovault.core.domain.model.Tag

internal fun TagEntity.toDomain(): Tag = Tag(
    id = TagId(id),
    name = name,
)

internal fun Tag.toEntity(): TagEntity = TagEntity(
    id = id.value,
    name = name,
)