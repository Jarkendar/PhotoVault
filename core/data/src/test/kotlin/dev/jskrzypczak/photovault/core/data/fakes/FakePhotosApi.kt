package dev.jskrzypczak.photovault.core.data.fakes

import dev.jskrzypczak.photovault.core.network.api.PhotosApi
import dev.jskrzypczak.photovault.core.network.dto.photo.PhotoCountDto
import dev.jskrzypczak.photovault.core.network.dto.photo.PhotoDto
import dev.jskrzypczak.photovault.core.network.dto.photo.PhotoPageDto
import dev.jskrzypczak.photovault.core.network.dto.photo.PhotoPatchRequestDto

class FakePhotosApi(
    initialList: Result<PhotoPageDto> = Result.success(PhotoPageDto(items = emptyList(), nextCursor = null, hasMore = false)),
) : PhotosApi {

    var nextListResponse: Result<PhotoPageDto> = initialList
    var nextCountResponse: Result<PhotoCountDto> = Result.success(PhotoCountDto(count = 0))
    var nextGetResponse: Result<PhotoDto>? = null
    var nextPatchResponse: Result<PhotoDto>? = null
    var nextDeleteResponse: Result<Unit> = Result.success(Unit)

    var capturedCursor: String? = null

    val listCalls = mutableListOf<ListCall>()
    val countCalls = mutableListOf<CountCall>()
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
        val matchMode: String?,
        val dateFrom: String?,
        val dateTo: String?,
    )

    data class CountCall(
        val q: String?,
        val tagIds: List<String>?,
        val categoryIds: List<String>?,
        val labelIds: List<String>?,
        val favoritesOnly: Boolean,
        val uploadedBy: String?,
        val matchMode: String?,
        val dateFrom: String?,
        val dateTo: String?,
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
        matchMode: String?,
        dateFrom: String?,
        dateTo: String?,
    ): Result<PhotoPageDto> {
        capturedCursor = cursor
        listCalls += ListCall(cursor, limit, q, tagIds, categoryIds, labelIds, favoritesOnly, uploadedBy, matchMode, dateFrom, dateTo)
        return nextListResponse
    }

    override suspend fun countPhotos(
        q: String?,
        tagIds: List<String>?,
        categoryIds: List<String>?,
        labelIds: List<String>?,
        favoritesOnly: Boolean,
        uploadedBy: String?,
        matchMode: String?,
        dateFrom: String?,
        dateTo: String?,
    ): Result<PhotoCountDto> {
        countCalls += CountCall(q, tagIds, categoryIds, labelIds, favoritesOnly, uploadedBy, matchMode, dateFrom, dateTo)
        return nextCountResponse
    }

    override suspend fun getPhoto(id: String): Result<PhotoDto> =
        nextGetResponse ?: error("FakePhotosApi.getPhoto unexpected call for id=$id")

    override suspend fun patchPhoto(id: String, request: PhotoPatchRequestDto): Result<PhotoDto> {
        patchCalls += id to request
        return nextPatchResponse ?: error("FakePhotosApi.patchPhoto unexpected call for id=$id")
    }

    override suspend fun deletePhoto(id: String): Result<Unit> = nextDeleteResponse
}
