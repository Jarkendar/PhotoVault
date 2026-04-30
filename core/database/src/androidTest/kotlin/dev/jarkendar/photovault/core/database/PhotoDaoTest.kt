package dev.jarkendar.photovault.core.database

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import dev.jarkendar.photovault.core.database.entity.PhotoEntity
import dev.jarkendar.photovault.core.database.entity.TagEntity
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Instant
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class PhotoDaoTest {

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
    fun upsert_and_observe_returns_correct_data() = runTest {
        db.photoDao().upsertPhotos(listOf(buildPhoto("1")))

        db.photoDao().observeAllWithRelations().test {
            val items = awaitItem()
            assertEquals(1, items.size)
            assertEquals("1", items[0].photo.id)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun setFavorite_updates_isFavorite_field() = runTest {
        db.photoDao().upsertPhotos(listOf(buildPhoto("2", isFavorite = false)))
        db.photoDao().setFavorite("2", true)

        db.photoDao().observeAllWithRelations().test {
            assertTrue(awaitItem()[0].photo.isFavorite)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun deletePhoto_cascades_junction_rows() = runTest {
        db.photoDao().upsertPhotos(listOf(buildPhoto("3")))
        db.tagDao().upsert(listOf(TagEntity(id = "t1", name = "nature")))
        db.openHelper.writableDatabase.execSQL(
            "INSERT INTO photo_tags (photoId, tagId) VALUES ('3', 't1')"
        )

        db.photoDao().deletePhoto("3")

        val cursor = db.openHelper.readableDatabase
            .query("SELECT * FROM photo_tags WHERE photoId = '3'")
        assertEquals(0, cursor.count)
        cursor.close()

        db.photoDao().observeAllWithRelations().test {
            assertTrue(awaitItem().isEmpty())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun replacePhotoTags_replaces_existing_associations() = runTest {
        db.photoDao().upsertPhotos(listOf(buildPhoto("p1")))
        db.tagDao().upsert(
            listOf(
                TagEntity(id = "t1", name = "morze"),
                TagEntity(id = "t2", name = "zachod"),
                TagEntity(id = "t3", name = "lato"),
            ),
        )

        db.photoDao().replacePhotoTags("p1", listOf("t1", "t2"))
        db.photoDao().replacePhotoTags("p1", listOf("t1", "t3"))

        val cursor = db.openHelper.readableDatabase
            .query("SELECT tagId FROM photo_tags WHERE photoId = 'p1' ORDER BY tagId")
        val remaining = mutableListOf<String>()
        while (cursor.moveToNext()) {
            remaining += cursor.getString(0)
        }
        cursor.close()
        assertEquals(listOf("t1", "t3"), remaining)
    }

    private fun buildPhoto(id: String, isFavorite: Boolean = false) = PhotoEntity(
        id = id,
        name = "photo_$id.jpg",
        sizeBytes = 1024L,
        mimeType = "image/jpeg",
        width = 1920,
        height = 1080,
        capturedAt = null,
        uploadedAt = Instant.fromEpochMilliseconds(1_000_000L),
        camera = null,
        latitude = null,
        longitude = null,
        placeName = null,
        isFavorite = isFavorite,
    )
}