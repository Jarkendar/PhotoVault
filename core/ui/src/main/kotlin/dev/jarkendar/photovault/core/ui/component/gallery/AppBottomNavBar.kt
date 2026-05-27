package dev.jarkendar.photovault.core.ui.component.gallery

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import dev.jarkendar.photovault.core.ui.R
import dev.jarkendar.photovault.core.ui.preview.PhonePreview
import dev.jarkendar.photovault.core.ui.theme.PhotoVaultTheme

enum class GalleryDestination {
    GALLERY, SEARCH, UPLOAD, MANAGE, SETTINGS
}

@Composable
fun AppBottomNavBar(
    selectedDestination: GalleryDestination,
    onSelect: (GalleryDestination) -> Unit,
    modifier: Modifier = Modifier,
) {
    NavigationBar(modifier = modifier) {
        NavigationBarItem(
            selected = selectedDestination == GalleryDestination.GALLERY,
            onClick = { onSelect(GalleryDestination.GALLERY) },
            icon = { Icon(imageVector = Icons.Default.PhotoLibrary, contentDescription = null) },
            label = { Text(stringResource(R.string.core_ui_nav_gallery)) },
        )
        NavigationBarItem(
            selected = selectedDestination == GalleryDestination.SEARCH,
            onClick = { onSelect(GalleryDestination.SEARCH) },
            icon = { Icon(imageVector = Icons.Default.Search, contentDescription = null) },
            label = { Text(stringResource(R.string.core_ui_nav_search)) },
        )
        NavigationBarItem(
            selected = selectedDestination == GalleryDestination.UPLOAD,
            onClick = { onSelect(GalleryDestination.UPLOAD) },
            icon = { Icon(imageVector = Icons.Default.CloudUpload, contentDescription = null) },
            label = { Text(stringResource(R.string.core_ui_nav_upload)) },
        )
        NavigationBarItem(
            selected = selectedDestination == GalleryDestination.MANAGE,
            onClick = { onSelect(GalleryDestination.MANAGE) },
            icon = { Icon(imageVector = Icons.Default.FolderOpen, contentDescription = null) },
            label = { Text(stringResource(R.string.core_ui_nav_manage)) },
        )
        NavigationBarItem(
            selected = selectedDestination == GalleryDestination.SETTINGS,
            onClick = { onSelect(GalleryDestination.SETTINGS) },
            icon = { Icon(imageVector = Icons.Default.Settings, contentDescription = null) },
            label = { Text(stringResource(R.string.core_ui_nav_settings)) },
        )
    }
}

@PhonePreview
@Composable
private fun AppBottomNavBarGalleryPreview() {
    PhotoVaultTheme {
        AppBottomNavBar(selectedDestination = GalleryDestination.GALLERY, onSelect = {})
    }
}

@PhonePreview
@Composable
private fun AppBottomNavBarSearchPreview() {
    PhotoVaultTheme {
        AppBottomNavBar(selectedDestination = GalleryDestination.SEARCH, onSelect = {})
    }
}
