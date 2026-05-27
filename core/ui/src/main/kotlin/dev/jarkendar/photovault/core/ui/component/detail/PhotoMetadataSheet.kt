package dev.jarkendar.photovault.core.ui.component.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.jarkendar.photovault.core.domain.model.Photo
import dev.jarkendar.photovault.core.domain.model.Tag
import dev.jarkendar.photovault.core.ui.R
import dev.jarkendar.photovault.core.ui.component.TagChip
import dev.jarkendar.photovault.core.ui.preview.PhonePreview
import dev.jarkendar.photovault.core.ui.preview.previewDetailPhoto
import dev.jarkendar.photovault.core.ui.theme.PhotoVaultTheme
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Composable
fun PhotoMetadataSheet(
    photo: Photo,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            .background(MaterialTheme.colorScheme.surface),
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 8.dp, bottom = 12.dp)
                .size(width = 32.dp, height = 4.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)),
        )
        Text(
            text = photo.name,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp),
        )
        val subtitle = buildSubtitle(photo)
        if (subtitle.isNotEmpty()) {
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 2.dp),
            )
        }
        photo.location?.placeName?.let { place ->
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = place,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(start = 4.dp),
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        if (photo.categories.isNotEmpty()) {
            PhotoDetailSection(title = stringResource(R.string.core_ui_section_categories)) {
                CategoryChipRow(categories = photo.categories, onAddClick = {})
            }
        }
        if (photo.tags.isNotEmpty()) {
            PhotoDetailSection(title = stringResource(R.string.core_ui_section_tags)) {
                TagsRow(tags = photo.tags)
            }
        }
        if (photo.labels.isNotEmpty()) {
            PhotoDetailSection(title = stringResource(R.string.core_ui_section_labels)) {
                LabelColorRow(labels = photo.labels)
            }
        }
        PhotoDetailSection(title = stringResource(R.string.core_ui_section_info)) {
            PhotoInfoCard(photo = photo)
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@PhonePreview
@Composable
private fun PhotoMetadataSheetPreview() {
    PhotoVaultTheme {
        PhotoMetadataSheet(photo = previewDetailPhoto())
    }
}

@Composable
private fun PhotoDetailSection(
    title: String,
    content: @Composable () -> Unit,
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(top = 16.dp, bottom = 8.dp),
        )
        content()
    }
}

@Composable
private fun TagsRow(tags: List<Tag>) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
    ) {
        items(tags, key = { it.id.value }) { tag ->
            TagChip(tag = tag)
        }
        item {
            SuggestionChip(
                onClick = {},
                label = { Text(stringResource(R.string.core_ui_add)) },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                    )
                },
            )
        }
    }
}

private fun buildSubtitle(photo: Photo): String = buildString {
    photo.capturedAt?.let { instant ->
        val ldt = instant.toLocalDateTime(TimeZone.currentSystemDefault())
        append("%04d-%02d-%02d".format(ldt.year, ldt.monthNumber, ldt.dayOfMonth))
    }
    val mb = "%.1f MB".format(photo.sizeBytes / (1024.0 * 1024.0))
    if (isNotEmpty()) append(" · ")
    append(mb)
    photo.camera?.let {
        append(" · ")
        append(it)
    }
}
