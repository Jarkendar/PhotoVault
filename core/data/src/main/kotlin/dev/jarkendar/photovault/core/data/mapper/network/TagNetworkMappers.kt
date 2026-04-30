package dev.jarkendar.photovault.core.data.mapper.network

import dev.jarkendar.photovault.core.domain.id.TagId
import dev.jarkendar.photovault.core.domain.model.Tag
import dev.jarkendar.photovault.core.network.dto.tag.TagDto

internal fun TagDto.toDomain(): Tag = Tag(
    id = TagId(id),
    name = name,
)