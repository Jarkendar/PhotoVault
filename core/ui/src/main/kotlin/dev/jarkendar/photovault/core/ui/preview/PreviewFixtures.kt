package dev.jarkendar.photovault.core.ui.preview

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import dev.jarkendar.photovault.core.domain.id.PhotoId
import dev.jarkendar.photovault.core.domain.id.TagId
import dev.jarkendar.photovault.core.domain.model.Photo
import dev.jarkendar.photovault.core.domain.model.Tag
import dev.jarkendar.photovault.core.ui.component.PhotoGrid
import dev.jarkendar.photovault.core.ui.component.TagChip
import dev.jarkendar.photovault.core.ui.state.EmptyState
import dev.jarkendar.photovault.core.ui.state.ErrorState
import dev.jarkendar.photovault.core.ui.state.LoadingState
import dev.jarkendar.photovault.core.ui.theme.PhotoVaultTheme
import kotlinx.datetime.Instant

internal fun previewPhotos(): List<Photo> = List(6) { i ->
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
        tags = if (i % 2 == 0) listOf(previewTags().first()) else emptyList(),
        categories = emptyList(),
        labels = emptyList(),
        isFavorite = i % 3 == 0,
    )
}

internal fun previewTags(): List<Tag> = listOf(
    Tag(id = TagId("tag_1"), name = "#morze"),
    Tag(id = TagId("tag_2"), name = "#góry"),
    Tag(id = TagId("tag_3"), name = "#miasto"),
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
