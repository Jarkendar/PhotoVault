package dev.jarkendar.photovault.core.data.repository

import dev.jarkendar.photovault.core.common.AppDispatchers
import dev.jarkendar.photovault.core.data.mapper.database.toDomain
import dev.jarkendar.photovault.core.data.mapper.database.toEntity
import dev.jarkendar.photovault.core.data.mapper.network.toDomain
import dev.jarkendar.photovault.core.data.mapper.network.toDomainError
import dev.jarkendar.photovault.core.database.dao.CategoryDao
import dev.jarkendar.photovault.core.database.dao.LabelDao
import dev.jarkendar.photovault.core.database.dao.PhotoDao
import dev.jarkendar.photovault.core.database.dao.TagDao
import dev.jarkendar.photovault.core.domain.error.DomainError
import dev.jarkendar.photovault.core.domain.id.PhotoId
import dev.jarkendar.photovault.core.domain.model.Photo
import dev.jarkendar.photovault.core.domain.query.SearchQuery
import dev.jarkendar.photovault.core.domain.repository.LoadMoreResult
import dev.jarkendar.photovault.core.domain.repository.PhotoRepository
import dev.jarkendar.photovault.core.network.api.PhotosApi
import dev.jarkendar.photovault.core.network.dto.photo.PhotoPageDto
import dev.jarkendar.photovault.core.network.dto.photo.PhotoPatchRequestDto
import dev.jarkendar.photovault.core.network.error.NetworkError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class PhotoRepositoryImpl(
    private val photoDao: PhotoDao,
    private val tagDao: TagDao,
    private val categoryDao: CategoryDao,
    private val labelDao: LabelDao,
    private val photosApi: PhotosApi,
    private val dispatchers: AppDispatchers,
) : PhotoRepository {

    override fun observePhotos(): Flow<List<Photo>> =
        photoDao.observeAllWithRelations()
            .map { list -> list.map { it.toDomain() } }
            .flowOn(dispatchers.default)

    override fun observePhoto(id: PhotoId): Flow<Photo?> =
        photoDao.observeByIdWithRelations(id.value)
            .map { it?.toDomain() }
            .flowOn(dispatchers.default)

    // Fetches head page only (no cursor). Full pagination goes through loadMorePhotos.
    override suspend fun refreshGallery(): Result<Unit> = withContext(dispatchers.io) {
        runCatching {
            val page = photosApi.listPhotos(limit = 100).getOrThrow()
            persistPage(page)
        }.recoverCatching { throw it.toDomain() }
    }

    override suspend fun loadMorePhotos(cursor: String): Result<LoadMoreResult> =
        withContext(dispatchers.io) {
            runCatching {
                val page = photosApi.listPhotos(cursor = cursor, limit = 100).getOrThrow()
                persistPage(page)
                LoadMoreResult(nextCursor = page.nextCursor, hasMore = page.hasMore)
            }.recoverCatching { throw it.toDomain() }
        }

    private suspend fun persistPage(page: PhotoPageDto) {
        val photos = page.items.map { it.toDomain() }
        val tags = photos.flatMap { it.tags }.distinctBy { it.id.value }
        val categories = photos.flatMap { it.categories }.distinctBy { it.id.value }
        val labels = photos.flatMap { it.labels }.distinctBy { it.id.value }

        tagDao.upsert(tags.map { it.toEntity() })
        categoryDao.upsert(categories.map { it.toEntity() })
        labelDao.upsert(labels.map { it.toEntity() })
        photoDao.upsertPhotos(photos.map { it.toEntity() })

        photos.forEach { photo ->
            photoDao.replacePhotoTags(photo.id.value, photo.tags.map { it.id.value })
            photoDao.replacePhotoCategories(photo.id.value, photo.categories.map { it.id.value })
            photoDao.replacePhotoLabels(photo.id.value, photo.labels.map { it.id.value })
        }
    }

    override suspend fun toggleFavorite(id: PhotoId): Result<Unit> = withContext(dispatchers.io) {
        runCatching {
            val current = photoDao.observeByIdWithRelations(id.value).firstOrNull()
                ?: throw DomainError.NotFound
            val previous = current.photo.isFavorite
            val next = !previous

            photoDao.setFavorite(id.value, next)
            try {
                photosApi.patchPhoto(id.value, PhotoPatchRequestDto(isFavorite = next)).getOrThrow()
            } catch (t: Throwable) {
                photoDao.setFavorite(id.value, previous)
                throw t
            }
            Unit
        }.recoverCatching { throw it.toDomain() }
    }

    override suspend fun search(query: SearchQuery): Result<List<Photo>> = withContext(dispatchers.io) {
        runCatching {
            val page = photosApi.listPhotos(
                q = query.text.ifBlank { null },
                tagIds = query.tagIds.map { it.value }.takeIf { it.isNotEmpty() },
                categoryIds = query.categoryIds.map { it.value }.takeIf { it.isNotEmpty() },
                labelIds = query.labelIds.map { it.value }.takeIf { it.isNotEmpty() },
                favoritesOnly = query.favoritesOnly,
            ).getOrThrow()
            page.toDomain()
        }.recoverCatching { throw it.toDomain() }
    }
}

private fun Throwable.toDomain(): DomainError = when (this) {
    is DomainError -> this
    is NetworkError -> toDomainError()
    else -> DomainError.Unknown(this)
}
