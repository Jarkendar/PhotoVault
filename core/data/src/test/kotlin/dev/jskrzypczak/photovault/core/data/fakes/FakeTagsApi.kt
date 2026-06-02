package dev.jskrzypczak.photovault.core.data.fakes

import dev.jskrzypczak.photovault.core.network.api.TagsApi
import dev.jskrzypczak.photovault.core.network.dto.tag.TagCreateRequestDto
import dev.jskrzypczak.photovault.core.network.dto.tag.TagDto
import dev.jskrzypczak.photovault.core.network.dto.tag.TagListDto
import dev.jskrzypczak.photovault.core.network.dto.tag.TagUpdateRequestDto

class FakeTagsApi(
    initialList: Result<TagListDto> = Result.success(TagListDto(items = emptyList())),
) : TagsApi {
    var nextListResponse: Result<TagListDto> = initialList

    override suspend fun listTags(usedOnly: Boolean): Result<TagListDto> = nextListResponse

    override suspend fun createTag(request: TagCreateRequestDto): Result<TagDto> =
        error("FakeTagsApi.createTag not expected")

    override suspend fun patchTag(id: String, request: TagUpdateRequestDto): Result<TagDto> =
        error("FakeTagsApi.patchTag not expected")

    override suspend fun deleteTag(id: String): Result<Unit> =
        error("FakeTagsApi.deleteTag not expected")
}
