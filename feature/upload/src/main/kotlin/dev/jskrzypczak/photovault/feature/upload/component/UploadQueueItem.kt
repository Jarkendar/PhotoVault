package dev.jskrzypczak.photovault.feature.upload.component

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import dev.jskrzypczak.photovault.feature.upload.R
import dev.jskrzypczak.photovault.feature.upload.UploadItemStatus
import dev.jskrzypczak.photovault.feature.upload.UploadItemUiState
import java.util.UUID
import kotlin.math.roundToInt
import kotlinx.collections.immutable.persistentListOf

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun UploadQueueItem(
    item: UploadItemUiState,
    onCancel: (UUID) -> Unit,
    modifier: Modifier = Modifier,
) {
    val isActive = item.status == UploadItemStatus.UPLOADING || item.status == UploadItemStatus.PROCESSING

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        ) {
            Thumbnail(uri = item.contentUri, status = item.status)
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.fileName,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = buildStatusLine(item),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                if (item.mlTags.isNotEmpty()) {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.padding(top = 4.dp),
                    ) {
                        item.mlTags.forEach { tag ->
                            MlTagChip(tag = tag)
                        }
                    }
                }
            }
            StatusActionButton(item = item, onCancel = onCancel)
        }

        if (isActive) {
            LinearProgressIndicator(
                progress = { item.progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 8.dp),
            )
        }
    }
}

@Composable
private fun Thumbnail(uri: String, status: UploadItemStatus) {
    val shape = RoundedCornerShape(8.dp)
    Box(
        modifier = Modifier
            .size(56.dp)
            .clip(shape),
        contentAlignment = Alignment.Center,
    ) {
        AsyncImage(
            model = Uri.parse(uri),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.matchParentSize(),
        )
        if (status == UploadItemStatus.DONE) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(Color(0xFF388E3C).copy(alpha = 0.75f)),
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .background(Color(0xFF4CAF50), CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun MlTagChip(tag: String) {
    AssistChip(
        onClick = {},
        label = { Text(text = "#$tag", style = MaterialTheme.typography.labelSmall) },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = null,
                modifier = Modifier.size(AssistChipDefaults.IconSize),
            )
        },
    )
}

@Composable
private fun StatusActionButton(item: UploadItemUiState, onCancel: (UUID) -> Unit) {
    when (item.status) {
        UploadItemStatus.DONE -> Icon(
            imageVector = Icons.Default.Check,
            contentDescription = null,
            tint = Color(0xFF4CAF50),
            modifier = Modifier.padding(8.dp),
        )
        UploadItemStatus.UPLOADING, UploadItemStatus.PROCESSING -> IconButton(onClick = {}) {
            Icon(
                imageVector = Icons.Default.Pause,
                contentDescription = stringResource(R.string.feature_upload_pause),
            )
        }
        UploadItemStatus.QUEUED, UploadItemStatus.FAILED -> IconButton(
            onClick = { onCancel(item.workId) },
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = stringResource(R.string.feature_upload_cancel),
            )
        }
        UploadItemStatus.CANCELLED -> Unit
    }
}

@Composable
private fun buildStatusLine(item: UploadItemUiState): String {
    val size = formatFileSize(item.sizeBytes)
    return when (item.status) {
        UploadItemStatus.QUEUED -> "$size · ${stringResource(R.string.feature_upload_status_queued)}"
        UploadItemStatus.UPLOADING, UploadItemStatus.PROCESSING ->
            "$size · ${(item.progress * 100).roundToInt()}%"
        UploadItemStatus.DONE -> "$size · ${stringResource(R.string.feature_upload_status_done)}"
        UploadItemStatus.FAILED -> "$size · ${stringResource(R.string.feature_upload_status_failed)}"
        UploadItemStatus.CANCELLED -> "$size · ${stringResource(R.string.feature_upload_status_cancelled)}"
    }
}

private fun formatFileSize(bytes: Long): String {
    val mb = bytes.toDouble() / (1024 * 1024)
    return if (mb >= 1.0) "%.1f MB".format(mb) else "${bytes / 1024} KB"
}
