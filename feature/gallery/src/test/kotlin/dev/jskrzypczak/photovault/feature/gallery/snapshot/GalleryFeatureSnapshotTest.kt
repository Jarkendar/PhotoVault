package dev.jskrzypczak.photovault.feature.gallery.snapshot

import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import dev.jskrzypczak.photovault.core.domain.id.CategoryId
import dev.jskrzypczak.photovault.core.domain.id.LabelId
import dev.jskrzypczak.photovault.core.domain.id.PhotoId
import dev.jskrzypczak.photovault.core.domain.id.TagId
import dev.jskrzypczak.photovault.core.domain.model.Category
import dev.jskrzypczak.photovault.core.domain.model.GeoLocation
import dev.jskrzypczak.photovault.core.domain.model.Label
import dev.jskrzypczak.photovault.core.domain.model.Photo
import dev.jskrzypczak.photovault.core.domain.model.Tag
import dev.jskrzypczak.photovault.core.ui.theme.PhotoVaultTheme
import dev.jskrzypczak.photovault.feature.gallery.GalleryScreen
import dev.jskrzypczak.photovault.feature.gallery.GalleryUiState
import dev.jskrzypczak.photovault.feature.gallery.PhotoDetailScreen
import dev.jskrzypczak.photovault.feature.gallery.PhotoDetailUiState
import kotlin.time.Instant
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
import org.junit.Rule
import org.junit.Test

class GalleryFeatureSnapshotTest {

    @get:Rule
    val paparazzi = Paparazzi(deviceConfig = DeviceConfig.PIXEL_6)

    @Test
    fun galleryScreenContent() = paparazzi.snapshot {
        PhotoVaultTheme(dynamicColor = false) {
            GalleryScreen(
                state = galleryContentState(),
                serverIp = "192.168.1.42",
                isServerConnected = true,
            )
        }
    }

    @Test
    fun galleryScreenContentDark() = paparazzi.snapshot {
        PhotoVaultTheme(darkTheme = true, dynamicColor = false) {
            GalleryScreen(
                state = galleryContentState(),
                serverIp = "192.168.1.42",
                isServerConnected = true,
            )
        }
    }

    @Test
    fun photoDetailScreenContent() = paparazzi.snapshot {
        PhotoVaultTheme(dynamicColor = false) {
            PhotoDetailScreen(state = PhotoDetailUiState.Content(photo = detailPhoto()))
        }
    }

    @Test
    fun photoDetailScreenContentDark() = paparazzi.snapshot {
        PhotoVaultTheme(darkTheme = true, dynamicColor = false) {
            PhotoDetailScreen(state = PhotoDetailUiState.Content(photo = detailPhoto()))
        }
    }
}

private fun galleryContentState(): GalleryUiState.Content {
    val categories = listOf(
        Category(CategoryId("cat_1"), "Natura", "#4CAF50"),
        Category(CategoryId("cat_2"), "Ludzie", "#2196F3"),
    )
    val dimensions = listOf(
        1080 to 1440, 1920 to 1080, 1080 to 1920,
        1920 to 1920, 1440 to 1080, 1080 to 1440,
        1920 to 1080, 1080 to 1920, 1920 to 1440,
        1440 to 1920, 1920 to 1080, 1080 to 1440,
    )
    val photos = dimensions.mapIndexed { i, (w, h) ->
        Photo(
            id = PhotoId("p$i"),
            name = "photo_$i.jpg",
            sizeBytes = 4_200_000L,
            mimeType = "image/jpeg",
            width = w,
            height = h,
            capturedAt = null,
            uploadedAt = Instant.fromEpochMilliseconds(0L),
            camera = null,
            location = null,
            tags = persistentListOf(),
            categories = persistentListOf(),
            labels = if (i % 3 == 0) persistentListOf(Label(LabelId("l$i"), "lbl", "#FF9800")) else persistentListOf(),
            isFavorite = i % 4 == 0,
        )
    }
    return GalleryUiState.Content(
        photos = persistentListOf(*photos.toTypedArray()),
        categories = persistentListOf(*categories.toTypedArray()),
        counts = persistentMapOf(CategoryId("cat_1") to 48, CategoryId("cat_2") to 73),
        selectedCategoryId = null,
        totalCount = 12,
        currentPage = 3,
        pages = persistentListOf(2, 3, 4),
    )
}

private fun detailPhoto(): Photo = Photo(
    id = PhotoId("detail_1"),
    name = "zachod_morze.jpg",
    sizeBytes = 4_200_000L,
    mimeType = "image/jpeg",
    width = 4000,
    height = 3000,
    capturedAt = Instant.fromEpochMilliseconds(1_776_525_840_000L),
    uploadedAt = Instant.fromEpochMilliseconds(1_776_525_840_000L),
    camera = "Pixel 8 Pro",
    location = GeoLocation(latitude = 54.40, longitude = 18.57, placeName = "Sopot, PL"),
    tags = persistentListOf(
        Tag(TagId("t1"), "#zachód-słońca"),
        Tag(TagId("t2"), "#morze"),
    ),
    categories = persistentListOf(
        Category(CategoryId("c1"), "Natura", "#4CAF50"),
        Category(CategoryId("c2"), "Podróże", "#2196F3"),
    ),
    labels = persistentListOf(
        Label(LabelId("l1"), "Czerwony", "#F44336"),
        Label(LabelId("l2"), "Pomarańczowy", "#FF9800"),
        Label(LabelId("l3"), "Żółty", "#FFEB3B"),
        Label(LabelId("l4"), "Zielony", "#4CAF50"),
        Label(LabelId("l5"), "Niebieski", "#2196F3"),
        Label(LabelId("l6"), "Fioletowy", "#9C27B0"),
    ),
    isFavorite = true,
)
