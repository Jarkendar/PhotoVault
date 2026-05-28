package dev.jskrzypczak.photovault.core.data.mapper.database

import dev.jskrzypczak.photovault.core.database.entity.LabelEntity
import dev.jskrzypczak.photovault.core.domain.id.LabelId
import dev.jskrzypczak.photovault.core.domain.model.Label

internal fun LabelEntity.toDomain(): Label = Label(
    id = LabelId(id),
    name = name,
    colorHex = colorHex,
)

internal fun Label.toEntity(): LabelEntity = LabelEntity(
    id = id.value,
    name = name,
    colorHex = colorHex,
)