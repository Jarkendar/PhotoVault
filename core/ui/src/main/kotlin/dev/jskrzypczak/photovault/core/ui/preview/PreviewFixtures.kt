package dev.jskrzypczak.photovault.core.ui.preview

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import dev.jskrzypczak.photovault.core.domain.id.CategoryId
import dev.jskrzypczak.photovault.core.domain.id.LabelId
import dev.jskrzypczak.photovault.core.domain.id.PhotoId
import dev.jskrzypczak.photovault.core.domain.id.TagId
import dev.jskrzypczak.photovault.core.domain.model.Category
import dev.jskrzypczak.photovault.core.domain.model.GeoLocation
import dev.jskrzypczak.photovault.core.domain.model.Label
import dev.jskrzypczak.photovault.core.domain.model.Photo
import dev.jskrzypczak.photovault.core.domain.model.Tag
import dev.jskrzypczak.photovault.core.ui.component.PhotoGrid
import dev.jskrzypczak.photovault.core.ui.component.TagChip
import dev.jskrzypczak.photovault.core.ui.state.EmptyState
import dev.jskrzypczak.photovault.core.ui.state.ErrorState
import dev.jskrzypczak.photovault.core.ui.state.LoadingState
import dev.jskrzypczak.photovault.core.ui.theme.PhotoVaultTheme
import kotlin.time.Instant
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

internal fun previewPhotos(): ImmutableList<Photo> = List(6) { i ->
    Photo(
        id = PhotoId("photo_$i"),
        name = "photo_$i.jpg",
        sizeBytes = 1_024_000L,
        mimeType = "image/jpeg",
        width = 1920,
        height = 1080,
        capturedAt = null,
        uploadedAt = Instant.fromEpochMilliseconds(0L),
        camera = null,
        location = null,
        tags = if (i % 2 == 0) persistentListOf(previewTags().first()) else persistentListOf(),
        categories = persistentListOf(),
        labels = persistentListOf(),
        isFavorite = i % 3 == 0,
        thumbnailUrl = "",
        mediumUrl = "",
        originalUrl = "",
    )
}.toImmutableList()

internal fun previewStaggeredPhotos(): ImmutableList<Photo> {
    val dimensions = listOf(
        1080 to 1440, 1920 to 1080, 1080 to 1920,
        1920 to 1920, 1440 to 1080, 1080 to 1440,
        1920 to 1080, 1080 to 1920, 1920 to 1440,
        1440 to 1920, 1920 to 1080, 1080 to 1440,
    )
    return dimensions.mapIndexed { i, (w, h) ->
        Photo(
            id = PhotoId("stag_$i"),
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
            labels = if (i % 3 == 0) persistentListOf(previewLabels().first()) else persistentListOf(),
            isFavorite = i % 4 == 0,
            thumbnailUrl = "",
            mediumUrl = "",
            originalUrl = "",
        )
    }.toImmutableList()
}

internal fun previewDetailPhoto(): Photo = Photo(
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
        Tag(id = TagId("tag_s"), name = "#zachód-słońca"),
        Tag(id = TagId("tag_m"), name = "#morze"),
    ),
    categories = previewCategories().take(2).toImmutableList(),
    labels = previewLabels(),
    isFavorite = true,
    thumbnailUrl = "",
    mediumUrl = "",
    originalUrl = "",
)

internal fun previewTags(): ImmutableList<Tag> = persistentListOf(
    Tag(id = TagId("tag_1"), name = "#morze"),
    Tag(id = TagId("tag_2"), name = "#góry"),
    Tag(id = TagId("tag_3"), name = "#miasto"),
)

internal fun previewCategories(): ImmutableList<Category> = persistentListOf(
    Category(id = CategoryId("cat_1"), name = "Natura", colorHex = "#4CAF50"),
    Category(id = CategoryId("cat_2"), name = "Podróże", colorHex = "#2196F3"),
    Category(id = CategoryId("cat_3"), name = "Ludzie", colorHex = "#FF5722"),
)

internal fun previewLabels(): ImmutableList<Label> = persistentListOf(
    Label(id = LabelId("lbl_1"), name = "Czerwony", colorHex = "#F44336"),
    Label(id = LabelId("lbl_2"), name = "Pomarańczowy", colorHex = "#FF9800"),
    Label(id = LabelId("lbl_3"), name = "Żółty", colorHex = "#FFEB3B"),
    Label(id = LabelId("lbl_4"), name = "Zielony", colorHex = "#4CAF50"),
    Label(id = LabelId("lbl_5"), name = "Niebieski", colorHex = "#2196F3"),
    Label(id = LabelId("lbl_6"), name = "Fioletowy", colorHex = "#9C27B0"),
)

@Preview(showBackground = true)
@Composable
internal fun PhotoGridPreview() {
    PhotoVaultTheme {
        PhotoGrid(photos = previewPhotos(), onPhotoClick = {})
    }
}

@Preview(showBackground = true)
@Composable
internal fun TagChipUnselectedPreview() {
    PhotoVaultTheme {
        TagChip(tag = previewTags().first())
    }
}

@Preview(showBackground = true)
@Composable
internal fun TagChipSelectedPreview() {
    PhotoVaultTheme {
        TagChip(tag = previewTags().first(), selected = true)
    }
}

@Preview(showBackground = true)
@Composable
internal fun LoadingStatePreview() {
    PhotoVaultTheme { LoadingState() }
}

@Preview(showBackground = true)
@Composable
internal fun EmptyStatePreview() {
    PhotoVaultTheme { EmptyState(message = "Brak zdjęć") }
}

@Preview(showBackground = true, name = "ErrorState bez retry")
@Composable
internal fun ErrorStatePreview() {
    PhotoVaultTheme { ErrorState(message = "Coś poszło nie tak") }
}

@Preview(showBackground = true, name = "ErrorState z retry")
@Composable
internal fun ErrorStateWithRetryPreview() {
    PhotoVaultTheme { ErrorState(message = "Coś poszło nie tak", onRetry = {}) }
}
