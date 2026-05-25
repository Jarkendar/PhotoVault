package dev.jarkendar.photovault.core.ui.snapshot

import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import dev.jarkendar.photovault.core.ui.component.PhotoGrid
import dev.jarkendar.photovault.core.ui.component.TagChip
import dev.jarkendar.photovault.core.ui.preview.previewPhotos
import dev.jarkendar.photovault.core.ui.preview.previewTags
import dev.jarkendar.photovault.core.ui.state.EmptyState
import dev.jarkendar.photovault.core.ui.state.ErrorState
import dev.jarkendar.photovault.core.ui.state.LoadingState
import dev.jarkendar.photovault.core.ui.theme.PhotoVaultTheme
import org.junit.Rule
import org.junit.Test

class CoreUiSnapshotTest {

    @get:Rule
    val paparazzi = Paparazzi(deviceConfig = DeviceConfig.PIXEL_6)

    @Test
    fun photoGrid() = paparazzi.snapshot {
        PhotoVaultTheme { PhotoGrid(photos = previewPhotos(), onPhotoClick = {}) }
    }

    @Test
    fun tagChipUnselected() = paparazzi.snapshot {
        PhotoVaultTheme { TagChip(tag = previewTags().first()) }
    }

    @Test
    fun tagChipSelected() = paparazzi.snapshot {
        PhotoVaultTheme { TagChip(tag = previewTags().first(), selected = true) }
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
}
