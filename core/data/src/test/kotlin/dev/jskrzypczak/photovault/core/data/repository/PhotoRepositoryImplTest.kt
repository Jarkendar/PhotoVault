package dev.jarkendar.photovault.core.data.repository

import app.cash.turbine.test
import dev.jarkendar.photovault.core.data.fakes.FakeCategoryDao
import dev.jarkendar.photovault.core.data.fakes.FakeLabelDao
import dev.jarkendar.photovault.core.data.fakes.FakePhotoDao
import dev.jarkendar.photovault.core.data.fakes.FakePhotosApi
import dev.jarkendar.photovault.core.data.fakes.FakeTagDao
import dev.jarkendar.photovault.core.data.fixtures.PhotoTestFixtures
import dev.jarkendar.photovault.core.data.fixtures.TestAppDispatchers
import dev.jarkendar.photovault.core.database.entity.PhotoTagCrossRef
import dev.jarkendar.photovault.core.domain.error.DomainError
import dev.jarkendar.photovault.core.domain.id.PhotoId
import dev.jarkendar.photovault.core.domain.id.TagId
import dev.jarkendar.photovault.core.domain.query.SearchQuery
import dev.jarkendar.photovault.core.domain.repository.LoadMoreResult
import dev.jarkendar.photovault.core.network.dto.photo.PhotoPageDto
import dev.jarkendar.photovault.core.network.error.NetworkError
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class PhotoRepositoryImplTest {

    private val SAMPLE_PHOTO_DTO = PhotoTestFixtures.PHOTO_DTO

    private val tagDao = FakeTagDao()
    private val categoryDao = FakeCategoryDao()
    private val labelDao = FakeLabelDao()
    private val photoDao = FakePhotoDao(
        tagSource = { tagDao.items.value },
        categorySource = { categoryDao.items.value },
        labelSource = { labelDao.items.value },
    )
    private val fakePhotosApi = FakePhotosApi()
    private val dispatchers = TestAppDispatchers()

    private val repository = PhotoRepositoryImpl(
        photoDao = photoDao,
        tagDao = tagDao,
        categoryDao = categoryDao,
        labelDao = labelDao,
        photosApi = fakePhotosApi,
        dispatchers = dispatchers,
    )

    @Test
    fun `observePhotos emits photos from DAO mapped to domain`() = runTest {
        tagDao.items.value = listOf(PhotoTestFixtures.TAG_ENTITY)
        categoryDao.items.value = listOf(PhotoTestFixtures.CATEGORY_ENTITY)
        labelDao.items.value = listOf(PhotoTestFixtures.LABEL_ENTITY)
        photoDao.photos.value = listOf(PhotoTestFixtures.PHOTO_ENTITY)
        photoDao.photoTags.value = listOf(PhotoTagCrossRef(photoId = "photo-abc123", tagId = "tag-001"))
        photoDao.photoCategories.value = listOf(
            dev.jarkendar.photovault.core.database.entity.PhotoCategoryCrossRef("photo-abc123", "cat-001"),
        )
        photoDao.photoLabels.value = listOf(
            dev.jarkendar.photovault.core.database.entity.PhotoLabelCrossRef("photo-abc123", "label-orange"),
        )

        repository.observePhotos().test {
            val items = awaitItem()
            assertEquals(1, items.size)
            assertEquals(PhotoTestFixtures.PHOTO_DOMAIN, items[0])
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `observePhoto by id returns matching photo or null`() = runTest {
        photoDao.photos.value = listOf(PhotoTestFixtures.PHOTO_ENTITY)

        repository.observePhoto(PhotoId("photo-abc123")).test {
            val item = assertNotNull(awaitItem())
            assertEquals("photo-abc123", item.id.value)
            cancelAndIgnoreRemainingEvents()
        }

        repository.observePhoto(PhotoId("does-not-exist")).test {
            assertNull(awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `refreshGallery success upserts photos and replaces junctions`() = runTest {
        fakePhotosApi.nextListResponse = Result.success(
            PhotoPageDto(items = listOf(PhotoTestFixtures.PHOTO_DTO), nextCursor = null, hasMore = false),
        )

        val result = repository.refreshGallery()

        assertTrue(result.isSuccess)
        assertEquals(1, photoDao.photos.value.size)
        assertEquals("photo-abc123", photoDao.photos.value.single().id)
        assertEquals(listOf("tag-001"), photoDao.photoTags.value.map { it.tagId })
        assertEquals(listOf("cat-001"), photoDao.photoCategories.value.map { it.categoryId })
        assertEquals(listOf("label-orange"), photoDao.photoLabels.value.map { it.labelId })
        assertEquals(1, tagDao.items.value.size)
        assertEquals(1, categoryDao.items.value.size)
        assertEquals(1, labelDao.items.value.size)
    }

    @Test
    fun `refreshGallery on network error returns failure with NoConnectivity`() = runTest {
        fakePhotosApi.nextListResponse = Result.failure(NetworkError.NoConnectivity)

        val result = repository.refreshGallery()

        assertTrue(result.isFailure)
        assertEquals(DomainError.NoConnectivity, result.exceptionOrNull())
        assertTrue(photoDao.photos.value.isEmpty())
    }

    @Test
    fun `toggleFavorite optimistically updates DB then syncs API`() = runTest {
        photoDao.photos.value = listOf(PhotoTestFixtures.PHOTO_ENTITY.copy(isFavorite = false))
        fakePhotosApi.nextPatchResponse = Result.success(PhotoTestFixtures.PHOTO_DTO.copy(isFavorite = true))

        val result = repository.toggleFavorite(PhotoId("photo-abc123"))

        assertTrue(result.isSuccess)
        assertTrue(photoDao.photos.value.single().isFavorite)
        val (id, request) = fakePhotosApi.patchCalls.single()
        assertEquals("photo-abc123", id)
        assertEquals(true, request.isFavorite)
    }

    @Test
    fun `toggleFavorite rolls back DB on API failure`() = runTest {
        photoDao.photos.value = listOf(PhotoTestFixtures.PHOTO_ENTITY.copy(isFavorite = false))
        fakePhotosApi.nextPatchResponse = Result.failure(NetworkError.NoConnectivity)

        val result = repository.toggleFavorite(PhotoId("photo-abc123"))

        assertTrue(result.isFailure)
        assertEquals(DomainError.NoConnectivity, result.exceptionOrNull())
        assertEquals(false, photoDao.photos.value.single().isFavorite)
    }

    @Test
    fun `toggleFavorite returns NotFound when photo is missing`() = runTest {
        val result = repository.toggleFavorite(PhotoId("missing"))

        assertTrue(result.isFailure)
        assertEquals(DomainError.NotFound, result.exceptionOrNull())
    }

    @Test
    fun `search delegates to API and returns mapped photos`() = runTest {
        fakePhotosApi.nextListResponse = Result.success(
            PhotoPageDto(items = listOf(PhotoTestFixtures.PHOTO_DTO), nextCursor = null, hasMore = false),
        )

        val result = repository.search(
            SearchQuery(
                text = "morze",
                tagIds = setOf(TagId("tag-001")),
                favoritesOnly = true,
            ),
        )

        assertTrue(result.isSuccess)
        assertEquals(listOf(PhotoTestFixtures.PHOTO_DOMAIN), result.getOrThrow())
        val call = fakePhotosApi.listCalls.single()
        assertEquals("morze", call.q)
        assertEquals(listOf("tag-001"), call.tagIds)
        assertEquals(true, call.favoritesOnly)
    }

    @Test
    fun `search with blank text passes null q`() = runTest {
        fakePhotosApi.nextListResponse = Result.success(
            PhotoPageDto(items = emptyList(), nextCursor = null, hasMore = false),
        )

        repository.search(SearchQuery(text = "  "))

        assertNull(fakePhotosApi.listCalls.single().q)
    }

    @Test
    fun `search returns failure with mapped DomainError on network error`() = runTest {
        fakePhotosApi.nextListResponse = Result.failure(NetworkError.NotFound("photo-not-found"))

        val result = repository.search(SearchQuery())

        assertTrue(result.isFailure)
        assertIs<DomainError.NotFound>(result.exceptionOrNull())
    }

    // --- pagination ---

    @Test
    fun `loadMorePhotos persists fetched photos to cache`() = runTest {
        // Given: cache empty, API returns page with 2 photos
        val photo1 = SAMPLE_PHOTO_DTO.copy(id = "photo-1")
        val photo2 = SAMPLE_PHOTO_DTO.copy(id = "photo-2")
        fakePhotosApi.nextListResponse = Result.success(
            PhotoPageDto(items = listOf(photo1, photo2), nextCursor = null, hasMore = false)
        )

        // When
        val result = repository.loadMorePhotos(cursor = "some-cursor")

        // Then: success + photos w cache
        assertTrue(result.isSuccess)
        val cached = repository.observePhotos().first()
        assertEquals(2, cached.size)
        assertTrue(cached.any { it.id.value == "photo-1" })
        assertTrue(cached.any { it.id.value == "photo-2" })
    }

    @Test
    fun `loadMorePhotos returns nextCursor and hasMore from API response`() = runTest {
        // Given
        fakePhotosApi.nextListResponse = Result.success(
            PhotoPageDto(items = listOf(SAMPLE_PHOTO_DTO), nextCursor = "next-cursor-xyz", hasMore = true)
        )

        // When
        val result = repository.loadMorePhotos(cursor = "current-cursor")

        // Then
        assertTrue(result.isSuccess)
        val loadResult = result.getOrNull()!!
        assertEquals("next-cursor-xyz", loadResult.nextCursor)
        assertTrue(loadResult.hasMore)
    }

    @Test
    fun `loadMorePhotos returns null cursor and hasMore=false on last page`() = runTest {
        // Given: API returns last page
        fakePhotosApi.nextListResponse = Result.success(
            PhotoPageDto(items = listOf(SAMPLE_PHOTO_DTO), nextCursor = null, hasMore = false)
        )

        // When
        val result = repository.loadMorePhotos(cursor = "some-cursor")

        // Then
        val loadResult = result.getOrNull()!!
        assertNull(loadResult.nextCursor)
        assertFalse(loadResult.hasMore)
    }

    @Test
    fun `loadMorePhotos passes cursor to API correctly`() = runTest {
        // Given
        val expectedCursor = "cursor-abc-123"
        fakePhotosApi.nextListResponse = Result.success(
            PhotoPageDto(items = emptyList(), nextCursor = null, hasMore = false)
        )

        // When
        repository.loadMorePhotos(cursor = expectedCursor)

        // Then
        assertEquals(expectedCursor, fakePhotosApi.capturedCursor)
    }

    @Test
    fun `loadMorePhotos preserves existing cache entries (does not delete photos from previous pages)`() = runTest {
        // Given: cache zawiera photo-old z poprzedniej strony
        val oldPhoto = SAMPLE_PHOTO_DTO.copy(id = "photo-old")
        fakePhotosApi.nextListResponse = Result.success(
            PhotoPageDto(items = listOf(oldPhoto), nextCursor = "cursor-1", hasMore = true)
        )
        repository.refreshGallery()

        // When: load more zwraca nowe photo
        val newPhoto = SAMPLE_PHOTO_DTO.copy(id = "photo-new")
        fakePhotosApi.nextListResponse = Result.success(
            PhotoPageDto(items = listOf(newPhoto), nextCursor = null, hasMore = false)
        )
        repository.loadMorePhotos(cursor = "cursor-1")

        // Then: cache zawiera obie zdjęcia
        val cached = repository.observePhotos().first()
        assertEquals(2, cached.size)
        assertTrue(cached.any { it.id.value == "photo-old" })
        assertTrue(cached.any { it.id.value == "photo-new" })
    }

    @Test
    fun `loadMorePhotos on network error returns Result failure with mapped DomainError`() = runTest {
        // Given
        fakePhotosApi.nextListResponse = Result.failure(NetworkError.NoConnectivity)

        // When
        val result = repository.loadMorePhotos(cursor = "any-cursor")

        // Then
        assertTrue(result.isFailure)
        assertEquals(DomainError.NoConnectivity, result.exceptionOrNull())
    }
}
