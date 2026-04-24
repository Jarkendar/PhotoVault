package dev.jarkendar.photovault.core.database

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import dev.jarkendar.photovault.core.database.entity.CategoryEntity
import dev.jarkendar.photovault.core.database.entity.PhotoEntity
import dev.jarkendar.photovault.core.database.entity.TagEntity
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Instant
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class PhotoWithRelationsTest {

    private lateinit var db: PhotoVaultDatabase

    @Before
    fun setup() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            PhotoVaultDatabase::class.java,
        )
            .allowMainThreadQueries()
            .build()
    }

    @After
    fun teardown() {
        db.close()
    }

    @Test
    fun photo_with_tags_and_categories_loads_all_relations() = runTest {
        db.photoDao().upsertPhotos(listOf(buildPhoto("p1")))
        db.tagDao().upsert(listOf(TagEntity(id = "tag1", name = "nature")))
        db.categoryDao().upsert(listOf(CategoryEntity(id = "cat1", name = "Landscape", colorHex = "#4CAF50")))
        db.openHelper.writableDatabase.execSQL(
            "INSERT INTO photo_tags (photoId, tagId) VALUES ('p1', 'tag1')"
        )
        db.openHelper.writableDatabase.execSQL(
            "INSERT INTO photo_categories (photoId, categoryId) VALUES ('p1', 'cat1')"
        )

        db.photoDao().observeAllWithRelations().test {
            val relations = awaitItem()[0]
            assertEquals(1, relations.tags.size)
            assertEquals("nature", relations.tags[0].name)
            assertEquals(1, relations.categories.size)
            assertEquals("Landscape", relations.categories[0].name)
            assertTrue(relations.labels.isEmpty())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun photo_without_relations_returns_empty_lists() = runTest {
        db.photoDao().upsertPhotos(listOf(buildPhoto("p2")))

        db.photoDao().observeAllWithRelations().test {
            val relations = awaitItem()[0]
            assertTrue(relations.tags.isEmpty())
            assertTrue(relations.categories.isEmpty())
            assertTrue(relations.labels.isEmpty())
            cancelAndIgnoreRemainingEvents()
        }
    }

    private fun buildPhoto(id: String) = PhotoEntity(
        id = id,
        name = "photo_$id.jpg",
        sizeBytes = 2048L,
        mimeType = "image/jpeg",
        width = 4032,
        height = 3024,
        capturedAt = null,
        uploadedAt = Instant.fromEpochMilliseconds(2_000_000L),
        camera = null,
        latitude = null,
        longitude = null,
        placeName = null,
        isFavorite = false,
    )
}