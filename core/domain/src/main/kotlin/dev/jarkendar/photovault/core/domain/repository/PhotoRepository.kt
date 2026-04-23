package dev.jarkendar.photovault.core.domain.repository

import dev.jarkendar.photovault.core.domain.id.PhotoId
import dev.jarkendar.photovault.core.domain.model.Photo
import dev.jarkendar.photovault.core.domain.query.SearchQuery
import kotlinx.coroutines.flow.Flow

interface PhotoRepository {
    fun observePhotos(): Flow<List<Photo>>
    fun observePhoto(id: PhotoId): Flow<Photo?>
    suspend fun refreshGallery(): Result<Unit>
    suspend fun toggleFavorite(id: PhotoId): Result<Unit>
    suspend fun search(query: SearchQuery): Result<List<Photo>>
}
