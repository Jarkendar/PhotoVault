package dev.jskrzypczak.photovault.core.data.mapper.database

import dev.jskrzypczak.photovault.core.database.entity.TagEntity
import dev.jskrzypczak.photovault.core.domain.id.TagId
import dev.jskrzypczak.photovault.core.domain.model.Tag

internal fun TagEntity.toDomain(): Tag = Tag(
    id = TagId(id),
    name = name,
    photoCount = photoCount,
    autoEnabled = autoEnabled,
    rolledOut = rolledOut,
)

internal fun Tag.toEntity(): TagEntity = TagEntity(
    id = id.value,
    name = name,
    photoCount = photoCount,
    autoEnabled = autoEnabled,
    rolledOut = rolledOut,
)