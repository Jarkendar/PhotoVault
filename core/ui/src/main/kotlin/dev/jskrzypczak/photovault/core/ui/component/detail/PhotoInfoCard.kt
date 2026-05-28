package dev.jskrzypczak.photovault.core.ui.component.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import dev.jskrzypczak.photovault.core.domain.model.Photo
import dev.jskrzypczak.photovault.core.ui.preview.PhonePreview
import dev.jskrzypczak.photovault.core.ui.preview.previewDetailPhoto
import dev.jskrzypczak.photovault.core.ui.theme.PhotoVaultTheme
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Composable
fun PhotoInfoCard(
    photo: Photo,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            photo.camera?.let { cameraName ->
                PhotoInfoRow(
                    icon = Icons.Default.PhotoCamera,
                    primary = cameraName,
                    secondary = null,
                )
            }
            photo.capturedAt?.let { instant ->
                val ldt = instant.toLocalDateTime(TimeZone.currentSystemDefault())
                val date = "%04d-%02d-%02d".format(ldt.year, ldt.monthNumber, ldt.dayOfMonth)
                val time = "%02d:%02d".format(ldt.hour, ldt.minute)
                if (photo.camera != null) {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                }
                PhotoInfoRow(
                    icon = Icons.Default.CalendarToday,
                    primary = date,
                    secondary = time,
                )
            }
            val hasPrevRow = photo.camera != null || photo.capturedAt != null
            if (hasPrevRow) HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            val mbSize = "%.1f MB".format(photo.sizeBytes / (1024.0 * 1024.0))
            val ext = photo.mimeType.substringAfterLast('/').uppercase()
            PhotoInfoRow(
                icon = Icons.Default.Image,
                primary = "${photo.width} × ${photo.height}",
                secondary = "$mbSize · $ext",
            )
        }
    }
}

@PhonePreview
@Composable
private fun PhotoInfoCardPreview() {
    PhotoVaultTheme {
        PhotoInfoCard(photo = previewDetailPhoto())
    }
}

@Composable
private fun PhotoInfoRow(
    icon: ImageVector,
    primary: String,
    secondary: String?,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Column {
            Text(text = primary, style = MaterialTheme.typography.bodyMedium)
            secondary?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
