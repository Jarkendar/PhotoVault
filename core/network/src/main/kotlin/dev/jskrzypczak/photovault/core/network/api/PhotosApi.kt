package dev.jskrzypczak.photovault.core.network.api

import dev.jskrzypczak.photovault.core.network.dto.photo.PhotoCountDto
import dev.jskrzypczak.photovault.core.network.dto.photo.PhotoDto
import dev.jskrzypczak.photovault.core.network.dto.photo.PhotoPageDto
import dev.jskrzypczak.photovault.core.network.dto.photo.PhotoPatchRequestDto

interface PhotosApi {
    suspend fun listPhotos(
        cursor: String? = null,
        limit: Int = 30,
        q: String? = null,
        tagIds: List<String>? = null,
        categoryIds: List<String>? = null,
        labelIds: List<String>? = null,
        favoritesOnly: Boolean = false,
        uploadedBy: String? = null,
        matchMode: String? = null,
        dateFrom: String? = null,
        dateTo: String? = null,
    ): Result<PhotoPageDto>

    suspend fun countPhotos(
        q: String? = null,
        tagIds: List<String>? = null,
        categoryIds: List<String>? = null,
        labelIds: List<String>? = null,
        favoritesOnly: Boolean = false,
        uploadedBy: String? = null,
        matchMode: String? = null,
        dateFrom: String? = null,
        dateTo: String? = null,
    ): Result<PhotoCountDto>

    suspend fun getPhoto(id: String): Result<PhotoDto>
    suspend fun patchPhoto(id: String, request: PhotoPatchRequestDto): Result<PhotoDto>
    suspend fun deletePhoto(id: String): Result<Unit>
}