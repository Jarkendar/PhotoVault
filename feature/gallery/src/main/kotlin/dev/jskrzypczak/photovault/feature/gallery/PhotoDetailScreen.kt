package dev.jskrzypczak.photovault.feature.gallery

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.HourglassEmpty
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import dev.jskrzypczak.photovault.core.domain.id.CategoryId
import dev.jskrzypczak.photovault.core.domain.id.LabelId
import dev.jskrzypczak.photovault.core.domain.id.PhotoId
import dev.jskrzypczak.photovault.core.domain.id.TagId
import dev.jskrzypczak.photovault.core.domain.model.Category
import dev.jskrzypczak.photovault.core.domain.model.GeoLocation
import dev.jskrzypczak.photovault.core.domain.model.Label
import dev.jskrzypczak.photovault.core.domain.model.Photo
import dev.jskrzypczak.photovault.core.domain.model.ProcessingStatus
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
    onOpenInBrowser: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    when (state) {
        PhotoDetailUiState.Loading -> LoadingState(modifier = modifier.fillMaxSize())
        is PhotoDetailUiState.Error -> ErrorState(
            message = state.message.ifBlank { stringResource(R.string.feature_gallery_detail_not_found) },
            modifier = modifier.fillMaxSize(),
        )
        is PhotoDetailUiState.Content -> PhotoDetailContent(
            photo = state.photo,
            onBack = onBack,
            onShare = onShare,
            onFavoriteToggle = onFavoriteToggle,
            onOpenInBrowser = onOpenInBrowser,
            modifier = modifier,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PhotoDetailContent(
    photo: Photo,
    onBack: () -> Unit,
    onShare: () -> Unit,
    onFavoriteToggle: () -> Unit,
    onOpenInBrowser: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var moreMenuExpanded by remember { mutableStateOf(false) }
    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    @Suppress("DEPRECATION")
    val transformableState = rememberTransformableState { zoomChange, panChange, _ ->
        val newScale = (scale * zoomChange).coerceIn(1f, 5f)
        scale = newScale
        offset = if (newScale <= 1f) Offset.Zero else offset + panChange
    }

    BottomSheetScaffold(
        modifier = modifier,
        sheetPeekHeight = 200.dp,
        sheetDragHandle = null,
        sheetShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        sheetContent = {
            PhotoMetadataSheet(
                photo = photo,
                modifier = Modifier.verticalScroll(rememberScrollState()),
            )
        },
    ) { _ ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(photoPlaceholderColor(photo.id.value)),
        ) {
            val imageUrl = photo.mediumUrl.ifEmpty { photo.originalUrl.ifEmpty { photo.thumbnailUrl } }
            if (imageUrl.isNotEmpty()) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = photo.name,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxSize()
                        .transformable(state = transformableState)
                        .pointerInput(Unit) {
                            detectTapGestures(onDoubleTap = {
                                scale = 1f
                                offset = Offset.Zero
                            })
                        }
                        .graphicsLayer {
                            scaleX = scale
                            scaleY = scale
                            translationX = offset.x
                            translationY = offset.y
                        },
                )
            }

            PhotoDetailTopBar(
                isFavorite = photo.isFavorite,
                onBack = onBack,
                onShare = onShare,
                onFavoriteToggle = onFavoriteToggle,
                onMore = { moreMenuExpanded = true },
                modifier = Modifier.align(Alignment.TopStart),
            )

            if (photo.processingStatus == ProcessingStatus.PENDING_CATEGORIZATION) {
                Surface(
                    color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.85f),
                    shape = MaterialTheme.shapes.small,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = 12.dp, bottom = 212.dp),
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.HourglassEmpty,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.size(14.dp),
                        )
                        Text(
                            text = stringResource(R.string.feature_gallery_detail_pending_categorization),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                        )
                    }
                }
            }

            DropdownMenu(
                expanded = moreMenuExpanded,
                onDismissRequest = { moreMenuExpanded = false },
            ) {
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.feature_gallery_detail_open_in_browser)) },
                    onClick = {
                        moreMenuExpanded = false
                        onOpenInBrowser()
                    },
                )
            }
        }
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
    processingStatus = ProcessingStatus.PENDING_CATEGORIZATION,
    thumbnailUrl = "", mediumUrl = "", originalUrl = "",
)
