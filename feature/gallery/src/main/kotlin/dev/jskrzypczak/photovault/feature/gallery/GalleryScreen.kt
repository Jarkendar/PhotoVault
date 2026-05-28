package dev.jskrzypczak.photovault.feature.gallery

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.jskrzypczak.photovault.core.domain.id.CategoryId
import dev.jskrzypczak.photovault.core.domain.model.Category
import dev.jskrzypczak.photovault.core.domain.model.Label
import dev.jskrzypczak.photovault.core.domain.model.Photo
import dev.jskrzypczak.photovault.core.ui.component.gallery.AppBottomNavBar
import dev.jskrzypczak.photovault.core.ui.component.gallery.CategoryFilterRow
import dev.jskrzypczak.photovault.core.ui.component.gallery.GalleryDestination
import dev.jskrzypczak.photovault.core.ui.component.gallery.GalleryPaginationRow
import dev.jskrzypczak.photovault.core.ui.component.gallery.GalleryTopBar
import dev.jskrzypczak.photovault.core.ui.component.gallery.PhotoSearchBar
import dev.jskrzypczak.photovault.core.ui.component.gallery.StaggeredPhotoGrid
import dev.jskrzypczak.photovault.core.ui.preview.PhonePreview
import dev.jskrzypczak.photovault.core.ui.state.EmptyState
import dev.jskrzypczak.photovault.core.ui.state.ErrorState
import dev.jskrzypczak.photovault.core.ui.state.LoadingState
import dev.jskrzypczak.photovault.core.ui.theme.PhotoVaultTheme
import dev.jskrzypczak.photovault.core.domain.id.LabelId
import dev.jskrzypczak.photovault.core.domain.id.PhotoId
import kotlin.time.Instant
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf

@Composable
fun GalleryScreen(
    state: GalleryUiState,
    serverIp: String? = null,
    isServerConnected: Boolean = false,
    searchQuery: String = "",
    onSearchQueryChange: (String) -> Unit = {},
    onFilterClick: () -> Unit = {},
    onMenuClick: () -> Unit = {},
    onAvatarClick: () -> Unit = {},
    onPhotoClick: (Photo) -> Unit = {},
    onFavoriteClick: (Photo) -> Unit = {},
    onCategorySelect: (CategoryId?) -> Unit = {},
    onPageClick: (Int) -> Unit = {},
    onUploadClick: () -> Unit = {},
    onDestinationSelect: (GalleryDestination) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            GalleryTopBar(
                serverIp = serverIp,
                isConnected = isServerConnected,
                onMenuClick = onMenuClick,
                onAvatarClick = onAvatarClick,
            )
        },
        bottomBar = {
            AppBottomNavBar(
                selectedDestination = GalleryDestination.GALLERY,
                onSelect = onDestinationSelect,
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onUploadClick,
                icon = { Icon(imageVector = Icons.Default.CloudUpload, contentDescription = null) },
                text = { Text(stringResource(R.string.feature_gallery_upload)) },
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            PhotoSearchBar(
                query = searchQuery,
                onQueryChange = onSearchQueryChange,
                onFilterClick = onFilterClick,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            )
            when (state) {
                is GalleryUiState.Content -> {
                    CategoryFilterRow(
                        categories = state.categories,
                        counts = state.counts,
                        selectedCategoryId = state.selectedCategoryId,
                        onCategorySelect = onCategorySelect,
                    )
                    GalleryPaginationRow(
                        totalCount = state.totalCount,
                        pages = state.pages,
                        currentPage = state.currentPage,
                        onPageClick = onPageClick,
                    )
                    StaggeredPhotoGrid(
                        photos = state.photos,
                        onPhotoClick = onPhotoClick,
                        onFavoriteClick = onFavoriteClick,
                        modifier = Modifier.weight(1f),
                    )
                }
                GalleryUiState.Loading -> LoadingState(modifier = Modifier.weight(1f))
                GalleryUiState.Empty -> EmptyState(
                    message = stringResource(R.string.feature_gallery_empty),
                    modifier = Modifier.weight(1f),
                )
                is GalleryUiState.Error -> ErrorState(
                    message = state.message,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@PhonePreview
@Composable
private fun GalleryScreenContentPreview() {
    PhotoVaultTheme(dynamicColor = false) {
        GalleryScreen(state = previewGalleryContentState(), serverIp = "192.168.1.42", isServerConnected = true)
    }
}

@PhonePreview
@Composable
private fun GalleryScreenLoadingPreview() {
    PhotoVaultTheme(dynamicColor = false) {
        GalleryScreen(state = GalleryUiState.Loading)
    }
}

@PhonePreview
@Composable
private fun GalleryScreenEmptyPreview() {
    PhotoVaultTheme(dynamicColor = false) {
        GalleryScreen(state = GalleryUiState.Empty)
    }
}

@PhonePreview
@Composable
private fun GalleryScreenErrorPreview() {
    PhotoVaultTheme(dynamicColor = false) {
        GalleryScreen(state = GalleryUiState.Error("Nie można połączyć się z serwerem"))
    }
}

private fun previewGalleryContentState(): GalleryUiState.Content {
    val cat1 = Category(CategoryId("c1"), "Natura", "#4CAF50")
    val cat2 = Category(CategoryId("c2"), "Ludzie", "#2196F3")
    val dims = listOf(
        1080 to 1440, 1920 to 1080, 1080 to 1920,
        1920 to 1920, 1440 to 1080, 1080 to 1440,
        1920 to 1080, 1080 to 1920, 1920 to 1440,
        1440 to 1920, 1920 to 1080, 1080 to 1440,
    )
    val photos = dims.mapIndexed { i, (w, h) ->
        Photo(
            id = PhotoId("p$i"), name = "photo_$i.jpg", sizeBytes = 4_200_000L,
            mimeType = "image/jpeg", width = w, height = h,
            capturedAt = null, uploadedAt = Instant.fromEpochMilliseconds(0L),
            camera = null, location = null, tags = persistentListOf(), categories = persistentListOf(),
            labels = if (i % 3 == 0) persistentListOf(Label(LabelId("l$i"), "lbl", "#FF9800")) else persistentListOf(),
            isFavorite = i % 4 == 0,
        )
    }
    return GalleryUiState.Content(
        photos = persistentListOf(*photos.toTypedArray()),
        categories = persistentListOf(cat1, cat2),
        counts = persistentMapOf(cat1.id to 48, cat2.id to 73),
        selectedCategoryId = null, totalCount = 12, currentPage = 3, pages = persistentListOf(2, 3, 4),
    )
}
