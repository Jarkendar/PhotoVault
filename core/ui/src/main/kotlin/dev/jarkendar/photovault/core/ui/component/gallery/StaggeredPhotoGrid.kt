package dev.jarkendar.photovault.core.ui.component.gallery

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import dev.jarkendar.photovault.core.domain.model.Photo
import dev.jarkendar.photovault.core.ui.preview.PhonePreview
import dev.jarkendar.photovault.core.ui.preview.previewStaggeredPhotos
import dev.jarkendar.photovault.core.ui.theme.PhotoVaultTheme
import kotlinx.collections.immutable.ImmutableList

@Composable
fun StaggeredPhotoGrid(
    photos: ImmutableList<Photo>,
    onPhotoClick: (Photo) -> Unit,
    onFavoriteClick: (Photo) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(3),
        modifier = modifier.testTag("staggered_photo_grid"),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalItemSpacing = 2.dp,
    ) {
        items(photos, key = { it.id.value }) { photo ->
            PhotoGridTile(
                photo = photo,
                onClick = { onPhotoClick(photo) },
                onFavoriteClick = { onFavoriteClick(photo) },
            )
        }
    }
}

@PhonePreview
@Composable
private fun StaggeredPhotoGridPreview() {
    PhotoVaultTheme {
        StaggeredPhotoGrid(
            photos = previewStaggeredPhotos(),
            onPhotoClick = {},
            onFavoriteClick = {},
        )
    }
}
