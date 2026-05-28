package dev.jarkendar.photovault.core.network.api

import dev.jarkendar.photovault.core.network.dto.photo.PhotoDto
import dev.jarkendar.photovault.core.network.dto.photo.PhotoPageDto
import dev.jarkendar.photovault.core.network.dto.photo.PhotoPatchRequestDto

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
    ): Result<PhotoPageDto>

    suspend fun getPhoto(id: String): Result<PhotoDto>
    suspend fun patchPhoto(id: String, request: PhotoPatchRequestDto): Result<PhotoDto>
    suspend fun deletePhoto(id: String): Result<Unit>
}