package dev.jskrzypczak.photovault.feature.gallery

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.jskrzypczak.photovault.core.domain.id.CategoryId
import dev.jskrzypczak.photovault.core.domain.id.LabelId
import dev.jskrzypczak.photovault.core.domain.id.PhotoId
import dev.jskrzypczak.photovault.core.domain.id.TagId
import dev.jskrzypczak.photovault.core.domain.model.Category
import dev.jskrzypczak.photovault.core.domain.model.GeoLocation
import dev.jskrzypczak.photovault.core.domain.model.Label
import dev.jskrzypczak.photovault.core.domain.model.Photo
import dev.jskrzypczak.photovault.core.domain.model.Tag
import dev.jskrzypczak.photovault.core.ui.component.detail.PhotoDetailTopBar
import dev.jskrzypczak.photovault.core.ui.component.detail.PhotoMetadataSheet
import dev.jskrzypczak.photovault.core.ui.preview.PhonePreview
import dev.jskrzypczak.photovault.core.ui.state.ErrorState
import dev.jskrzypczak.photovault.core.ui.state.LoadingState
import dev.jskrzypczak.photovault.core.ui.theme.PhotoVaultTheme
import dev.jskrzypczak.photovault.core.ui.util.photoPlaceholderColor
import kotlin.time.Instant
import kotlinx.collections.immutable.persistentListOf

@Composable
fun PhotoDetailScreen(
    state: PhotoDetailUiState,
    onBack: () -> Unit = {},
    onShare: () -> Unit = {},
    onFavoriteToggle: () -> Unit = {},
    onMore: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    when (state) {
        PhotoDetailUiState.Loading -> LoadingState(modifier = modifier.fillMaxSize())
        is PhotoDetailUiState.Error -> ErrorState(message = state.message, modifier = modifier.fillMaxSize())
        is PhotoDetailUiState.Content -> PhotoDetailContent(
            photo = state.photo,
            onBack = onBack,
            onShare = onShare,
            onFavoriteToggle = onFavoriteToggle,
            onMore = onMore,
            modifier = modifier,
        )
    }
}

@Composable
private fun PhotoDetailContent(
    photo: Photo,
    onBack: () -> Unit,
    onShare: () -> Unit,
    onFavoriteToggle: () -> Unit,
    onMore: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
                .background(photoPlaceholderColor(photo.id.value)),
        ) {
            PhotoDetailTopBar(
                isFavorite = photo.isFavorite,
                onBack = onBack,
                onShare = onShare,
                onFavoriteToggle = onFavoriteToggle,
                onMore = onMore,
                modifier = Modifier.align(Alignment.TopStart),
            )
        }
        PhotoMetadataSheet(
            photo = photo,
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
        )
    }
}

@PhonePreview
@Composable
private fun PhotoDetailScreenContentPreview() {
    PhotoVaultTheme(dynamicColor = false) {
        PhotoDetailScreen(state = PhotoDetailUiState.Content(photo = previewDetailPhoto()))
    }
}

@PhonePreview
@Composable
private fun PhotoDetailScreenLoadingPreview() {
    PhotoVaultTheme(dynamicColor = false) {
        PhotoDetailScreen(state = PhotoDetailUiState.Loading)
    }
}

@PhonePreview
@Composable
private fun PhotoDetailScreenErrorPreview() {
    PhotoVaultTheme(dynamicColor = false) {
        PhotoDetailScreen(state = PhotoDetailUiState.Error("Nie można załadować zdjęcia"))
    }
}

private fun previewDetailPhoto() = Photo(
    id = PhotoId("detail_1"), name = "zachod_morze.jpg", sizeBytes = 4_200_000L,
    mimeType = "image/jpeg", width = 4000, height = 3000,
    capturedAt = Instant.fromEpochMilliseconds(1_776_525_840_000L),
    uploadedAt = Instant.fromEpochMilliseconds(1_776_525_840_000L),
    camera = "Pixel 8 Pro",
    location = GeoLocation(latitude = 54.40, longitude = 18.57, placeName = "Sopot, PL"),
    tags = persistentListOf(Tag(TagId("t1"), "#zachód-słońca"), Tag(TagId("t2"), "#morze")),
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
