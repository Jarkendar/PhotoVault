package dev.jskrzypczak.photovault.core.data.mapper.network

import dev.jskrzypczak.photovault.core.domain.id.LabelId
import dev.jskrzypczak.photovault.core.domain.model.Label
import dev.jskrzypczak.photovault.core.network.dto.label.LabelDto

internal fun LabelDto.toDomain(): Label = Label(
    id = LabelId(id),
    name = name.name.lowercase(),
    colorHex = colorHex,
    photoCount = photoCount,
)