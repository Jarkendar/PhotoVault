package dev.jskrzypczak.photovault.core.ui.component.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.jskrzypczak.photovault.core.ui.R
import dev.jskrzypczak.photovault.core.ui.preview.PhonePreview
import dev.jskrzypczak.photovault.core.ui.theme.PhotoVaultTheme

@Composable
fun PhotoDetailTopBar(
    isFavorite: Boolean,
    onBack: () -> Unit,
    onShare: () -> Unit,
    onFavoriteToggle: () -> Unit,
    onMore: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 4.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(R.string.core_ui_back),
                tint = Color.White,
            )
        }
        Row {
            IconButton(onClick = onShare) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = stringResource(R.string.core_ui_share),
                    tint = Color.White,
                )
            }
            IconButton(onClick = onFavoriteToggle) {
                Icon(
                    imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = stringResource(
                        if (isFavorite) R.string.core_ui_remove_favorite else R.string.core_ui_add_favorite,
                    ),
                    tint = if (isFavorite) Color(0xFFE91E8C) else Color.White,
                )
            }
            IconButton(onClick = onMore) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = stringResource(R.string.core_ui_more),
                    tint = Color.White,
                )
            }
        }
    }
}

@PhonePreview
@Composable
private fun PhotoDetailTopBarFavoritePreview() {
    PhotoVaultTheme {
        Box(modifier = Modifier.background(Color(0xFF5C6BC0))) {
            PhotoDetailTopBar(isFavorite = true, onBack = {}, onShare = {}, onFavoriteToggle = {}, onMore = {})
        }
    }
}

@PhonePreview
@Composable
private fun PhotoDetailTopBarNotFavoritePreview() {
    PhotoVaultTheme {
        Box(modifier = Modifier.background(Color(0xFF5C6BC0))) {
            PhotoDetailTopBar(isFavorite = false, onBack = {}, onShare = {}, onFavoriteToggle = {}, onMore = {})
        }
    }
}
