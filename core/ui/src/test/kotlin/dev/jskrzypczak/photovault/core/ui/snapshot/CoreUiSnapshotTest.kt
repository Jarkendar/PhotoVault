package dev.jskrzypczak.photovault.core.ui.snapshot

import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import dev.jskrzypczak.photovault.core.ui.component.PhotoGrid
import dev.jskrzypczak.photovault.core.ui.component.TagChip
import dev.jskrzypczak.photovault.core.ui.component.detail.CategoryChipRow
import dev.jskrzypczak.photovault.core.ui.component.detail.LabelColorRow
import dev.jskrzypczak.photovault.core.ui.component.detail.PhotoInfoCard
import dev.jskrzypczak.photovault.core.ui.component.detail.PhotoMetadataSheet
import dev.jskrzypczak.photovault.core.ui.component.detail.PhotoDetailTopBar
import dev.jskrzypczak.photovault.core.ui.component.gallery.AppBottomNavBar
import dev.jskrzypczak.photovault.core.ui.component.gallery.CategoryFilterRow
import dev.jskrzypczak.photovault.core.ui.component.gallery.GalleryDestination
import dev.jskrzypczak.photovault.core.ui.component.gallery.GalleryPaginationRow
import dev.jskrzypczak.photovault.core.ui.component.gallery.GalleryTopBar
import dev.jskrzypczak.photovault.core.ui.component.gallery.PhotoSearchBar
import dev.jskrzypczak.photovault.core.ui.component.gallery.StaggeredPhotoGrid
import dev.jskrzypczak.photovault.core.ui.preview.previewCategories
import dev.jskrzypczak.photovault.core.ui.preview.previewDetailPhoto
import dev.jskrzypczak.photovault.core.ui.preview.previewLabels
import dev.jskrzypczak.photovault.core.ui.preview.previewPhotos
import dev.jskrzypczak.photovault.core.ui.preview.previewStaggeredPhotos
import dev.jskrzypczak.photovault.core.ui.preview.previewTags
import dev.jskrzypczak.photovault.core.ui.state.EmptyState
import dev.jskrzypczak.photovault.core.ui.state.ErrorState
import dev.jskrzypczak.photovault.core.ui.state.LoadingState
import dev.jskrzypczak.photovault.core.ui.theme.PhotoVaultTheme
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
import org.junit.Rule
import org.junit.Test

class CoreUiSnapshotTest {

    @get:Rule
    val paparazzi = Paparazzi(deviceConfig = DeviceConfig.PIXEL_6)

    // ── existing component tests ──────────────────────────────────────────

    @Test
    fun photoGrid() = paparazzi.snapshot {
        PhotoVaultTheme { PhotoGrid(photos = previewPhotos(), onPhotoClick = {}) }
    }

    @Test
    fun tagChipAutoDisabled() = paparazzi.snapshot {
        // autoEnabled = false, rolledOut = true — plain chip, no icons
        PhotoVaultTheme { TagChip(tag = previewTags()[0]) }
    }

    @Test
    fun tagChipAutoEnabled() = paparazzi.snapshot {
        // autoEnabled = true — selected chip with SmartToy leading icon
        PhotoVaultTheme { TagChip(tag = previewTags()[1]) }
    }

    @Test
    fun tagChipProcessing() = paparazzi.snapshot {
        // rolledOut = false — chip with HourglassEmpty trailing icon
        PhotoVaultTheme { TagChip(tag = previewTags()[2]) }
    }

    @Test
    fun loadingState() = paparazzi.snapshot {
        PhotoVaultTheme { LoadingState() }
    }

    @Test
    fun emptyState() = paparazzi.snapshot {
        PhotoVaultTheme { EmptyState(message = "Brak zdjęć") }
    }

    @Test
    fun errorStateNoRetry() = paparazzi.snapshot {
        PhotoVaultTheme { ErrorState(message = "Coś poszło nie tak") }
    }

    @Test
    fun errorStateWithRetry() = paparazzi.snapshot {
        PhotoVaultTheme { ErrorState(message = "Coś poszło nie tak", onRetry = {}) }
    }

    // ── gallery components ────────────────────────────────────────────────

    @Test
    fun galleryTopBarConnected() = paparazzi.snapshot {
        PhotoVaultTheme {
            GalleryTopBar(
                serverIp = "192.168.1.42",
                isConnected = true,
                onMenuClick = {},
                onAvatarClick = {},
            )
        }
    }

    @Test
    fun galleryTopBarDisconnected() = paparazzi.snapshot {
        PhotoVaultTheme {
            GalleryTopBar(
                serverIp = "192.168.1.42",
                isConnected = false,
                onMenuClick = {},
                onAvatarClick = {},
            )
        }
    }

    @Test
    fun photoSearchBar() = paparazzi.snapshot {
        PhotoVaultTheme {
            PhotoSearchBar(query = "", onQueryChange = {}, onFilterClick = {})
        }
    }

    @Test
    fun categoryFilterRowAllSelected() = paparazzi.snapshot {
        PhotoVaultTheme {
            CategoryFilterRow(
                categories = previewCategories(),
                counts = persistentMapOf(),
                selectedCategoryId = null,
                onCategorySelect = {},
            )
        }
    }

    @Test
    fun staggeredPhotoGrid() = paparazzi.snapshot {
        PhotoVaultTheme {
            StaggeredPhotoGrid(
                photos = previewStaggeredPhotos(),
                onPhotoClick = {},
                onFavoriteClick = {},
            )
        }
    }

    @Test
    fun galleryPaginationRow() = paparazzi.snapshot {
        PhotoVaultTheme {
            GalleryPaginationRow(
                totalCount = 12,
                pages = persistentListOf(2, 3, 4),
                currentPage = 3,
                onPageClick = {},
            )
        }
    }

    @Test
    fun appBottomNavBarGallery() = paparazzi.snapshot {
        PhotoVaultTheme {
            AppBottomNavBar(
                selectedDestination = GalleryDestination.GALLERY,
                onSelect = {},
            )
        }
    }

    // ── detail components ─────────────────────────────────────────────────

    @Test
    fun photoDetailTopBarFavorite() = paparazzi.snapshot {
        PhotoVaultTheme {
            PhotoDetailTopBar(
                isFavorite = true,
                onBack = {},
                onShare = {},
                onFavoriteToggle = {},
                onMore = {},
            )
        }
    }

    @Test
    fun labelColorRow() = paparazzi.snapshot {
        PhotoVaultTheme { LabelColorRow(labels = previewLabels()) }
    }

    @Test
    fun categoryChipRow() = paparazzi.snapshot {
        PhotoVaultTheme {
            CategoryChipRow(categories = previewCategories(), onAddClick = {})
        }
    }

    @Test
    fun photoInfoCard() = paparazzi.snapshot {
        PhotoVaultTheme { PhotoInfoCard(photo = previewDetailPhoto()) }
    }

    @Test
    fun photoMetadataSheet() = paparazzi.snapshot {
        PhotoVaultTheme { PhotoMetadataSheet(photo = previewDetailPhoto()) }
    }

    @Test
    fun photoMetadataSheetDark() = paparazzi.snapshot {
        PhotoVaultTheme(darkTheme = true, dynamicColor = false) {
            PhotoMetadataSheet(photo = previewDetailPhoto())
        }
    }
}
