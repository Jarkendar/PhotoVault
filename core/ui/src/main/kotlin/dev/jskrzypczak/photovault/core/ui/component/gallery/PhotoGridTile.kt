package dev.jskrzypczak.photovault.core.ui.component.gallery

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import dev.jskrzypczak.photovault.core.domain.model.Photo
import dev.jskrzypczak.photovault.core.ui.R
import dev.jskrzypczak.photovault.core.ui.preview.PhonePreview
import dev.jskrzypczak.photovault.core.ui.preview.previewStaggeredPhotos
import dev.jskrzypczak.photovault.core.ui.theme.PhotoVaultTheme
import dev.jskrzypczak.photovault.core.ui.util.parseHexColor
import dev.jskrzypczak.photovault.core.ui.util.photoPlaceholderColor

internal object PhotoGridTileTags {
    const val TILE_PREFIX = "photo_tile_"
}

@Composable
fun PhotoGridTile(
    photo: Photo,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val aspectRatio = if (photo.height > 0) photo.width.toFloat() / photo.height else 1f
    Box(
        modifier = modifier
            .testTag("${PhotoGridTileTags.TILE_PREFIX}${photo.id.value}")
            .aspectRatio(aspectRatio)
            .background(photoPlaceholderColor(photo.id.value))
            .clickable(onClick = onClick),
    ) {
        if (photo.thumbnailUrl.isNotEmpty()) {
            AsyncImage(
                model = photo.thumbnailUrl,
                contentDescription = photo.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
        }

        val dotColor = photo.labels.firstOrNull()
            ?.let { parseHexColor(it.colorHex) }
            ?: MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(4.dp)
                .size(8.dp)
                .clip(CircleShape)
                .background(dotColor),
        )
        IconButton(
            onClick = onFavoriteClick,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(32.dp),
        ) {
            Icon(
                imageVector = if (photo.isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                contentDescription = stringResource(
                    if (photo.isFavorite) R.string.core_ui_remove_favorite else R.string.core_ui_add_favorite,
                ),
                tint = Color.White,
                modifier = Modifier.size(16.dp),
            )
        }
    }
}

@PhonePreview
@Composable
private fun PhotoGridTileFavoritePreview() {
    PhotoVaultTheme {
        PhotoGridTile(photo = previewStaggeredPhotos().first(), onClick = {}, onFavoriteClick = {})
    }
}

@PhonePreview
@Composable
private fun PhotoGridTileNotFavoritePreview() {
    PhotoVaultTheme {
        PhotoGridTile(photo = previewStaggeredPhotos()[1], onClick = {}, onFavoriteClick = {})
    }
}
