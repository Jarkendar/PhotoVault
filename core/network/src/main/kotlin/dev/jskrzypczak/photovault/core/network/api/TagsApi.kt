package dev.jarkendar.photovault.core.network.api

import dev.jarkendar.photovault.core.network.dto.tag.TagCreateRequestDto
import dev.jarkendar.photovault.core.network.dto.tag.TagDto
import dev.jarkendar.photovault.core.network.dto.tag.TagListDto
import dev.jarkendar.photovault.core.network.dto.tag.TagUpdateRequestDto

interface TagsApi {
    suspend fun listTags(usedOnly: Boolean = false): Result<TagListDto>
    suspend fun createTag(request: TagCreateRequestDto): Result<TagDto>
    suspend fun patchTag(id: String, request: TagUpdateRequestDto): Result<TagDto>
    suspend fun deleteTag(id: String): Result<Unit>
}