package dev.jarkendar.photovault.core.data.mapper.network

import dev.jarkendar.photovault.core.domain.id.LabelId
import dev.jarkendar.photovault.core.domain.model.Label
import dev.jarkendar.photovault.core.network.dto.label.LabelDto

internal fun LabelDto.toDomain(): Label = Label(
    id = LabelId(id),
    name = name.name.lowercase(),
    colorHex = colorHex,
)