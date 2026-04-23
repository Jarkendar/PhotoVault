package dev.jarkendar.photovault.core.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.jarkendar.photovault.core.domain.model.Photo

@Composable
fun PhotoGrid(
    photos: List<Photo>,
    onPhotoClick: (Photo) -> Unit,
    modifier: Modifier = Modifier,
    columns: Int = 3,
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(columns),
        modifier = modifier.testTag("photo_grid"),
    ) {
        items(photos, key = { it.id.value }) { photo ->
            PhotoItem(photo = photo, onClick = { onPhotoClick(photo) })
        }
    }
}

@Composable
private fun PhotoItem(photo: Photo, onClick: () -> Unit) {
    val aspectRatio = if (photo.height > 0) photo.width.toFloat() / photo.height else 1f
    Box(
        modifier = Modifier
            .testTag("photo_item_${photo.id.value}")
            .aspectRatio(aspectRatio)
            .background(photoPlaceholderColor(photo.id.value))
            .clickable(onClick = onClick),
    ) {
        if (photo.isFavorite) {
            Text(
                text = "♥",
                color = Color.White,
                fontSize = 14.sp,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(4.dp)
                    .size(20.dp),
            )
        }
    }
}

private fun photoPlaceholderColor(id: String): Color {
    val hue = (id.hashCode() and 0xFFFFFF) % 360
    return Color.hsl(hue.toFloat().coerceAtLeast(0f), saturation = 0.4f, lightness = 0.6f)
}
