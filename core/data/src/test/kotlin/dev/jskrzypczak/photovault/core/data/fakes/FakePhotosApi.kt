package dev.jarkendar.photovault.core.data.fakes

import dev.jarkendar.photovault.core.network.api.PhotosApi
import dev.jarkendar.photovault.core.network.dto.photo.PhotoDto
import dev.jarkendar.photovault.core.network.dto.photo.PhotoPageDto
import dev.jarkendar.photovault.core.network.dto.photo.PhotoPatchRequestDto

class FakePhotosApi(
    initialList: Result<PhotoPageDto> = Result.success(PhotoPageDto(items = emptyList(), nextCursor = null, hasMore = false)),
) : PhotosApi {

    var nextListResponse: Result<PhotoPageDto> = initialList
    var nextGetResponse: Result<PhotoDto>? = null
    var nextPatchResponse: Result<PhotoDto>? = null
    var nextDeleteResponse: Result<Unit> = Result.success(Unit)

    var capturedCursor: String? = null

    val listCalls = mutableListOf<ListCall>()
    val patchCalls = mutableListOf<Pair<String, PhotoPatchRequestDto>>()

    data class ListCall(
        val cursor: String?,
        val limit: Int,
        val q: String?,
        val tagIds: List<String>?,
        val categoryIds: List<String>?,
        val labelIds: List<String>?,
        val favoritesOnly: Boolean,
        val uploadedBy: String?,
    )

    override suspend fun listPhotos(
        cursor: String?,
        limit: Int,
        q: String?,
        tagIds: List<String>?,
        categoryIds: List<String>?,
        labelIds: List<String>?,
        favoritesOnly: Boolean,
        uploadedBy: String?,
    ): Result<PhotoPageDto> {
        capturedCursor = cursor
        listCalls += ListCall(cursor, limit, q, tagIds, categoryIds, labelIds, favoritesOnly, uploadedBy)
        return nextListResponse
    }

    override suspend fun getPhoto(id: String): Result<PhotoDto> =
        nextGetResponse ?: error("FakePhotosApi.getPhoto unexpected call for id=$id")

    override suspend fun patchPhoto(id: String, request: PhotoPatchRequestDto): Result<PhotoDto> {
        patchCalls += id to request
        return nextPatchResponse ?: error("FakePhotosApi.patchPhoto unexpected call for id=$id")
    }

    override suspend fun deletePhoto(id: String): Result<Unit> = nextDeleteResponse
}