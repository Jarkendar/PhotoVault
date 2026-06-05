package dev.jskrzypczak.photovault.core.data.mapper.network

import dev.jskrzypczak.photovault.core.domain.id.TagId
import dev.jskrzypczak.photovault.core.domain.model.Tag
import dev.jskrzypczak.photovault.core.network.dto.tag.TagDto

internal fun TagDto.toDomain(): Tag = Tag(
    id = TagId(id),
    name = name,
    photoCount = photoCount,
    autoEnabled = autoEnabled,
    rolledOut = rolledOut,
)