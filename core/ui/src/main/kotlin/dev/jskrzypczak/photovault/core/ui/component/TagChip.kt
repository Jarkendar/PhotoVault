package dev.jskrzypczak.photovault.core.ui.component

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.outlined.HourglassEmpty
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.jskrzypczak.photovault.core.domain.model.Tag
import dev.jskrzypczak.photovault.core.ui.R

/**
 * Chip displaying a [Tag]. Selecting the chip toggles [autoEnabled] (ML auto-assignment).
 * When [tag.rolledOut] is false the chip shows a "processing" hourglass trailing icon.
 */
@Composable
fun TagChip(
    tag: Tag,
    onToggleAutoEnabled: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    FilterChip(
        selected = tag.autoEnabled,
        onClick = onToggleAutoEnabled,
        label = { Text(text = tag.name) },
        leadingIcon = if (tag.autoEnabled) {
            {
                Icon(
                    imageVector = Icons.Default.SmartToy,
                    contentDescription = stringResource(R.string.core_ui_auto_tag_enabled),
                    modifier = Modifier.size(16.dp),
                )
            }
        } else null,
        trailingIcon = if (!tag.rolledOut) {
            {
                Icon(
                    imageVector = Icons.Outlined.HourglassEmpty,
                    contentDescription = stringResource(R.string.core_ui_tag_processing),
                    modifier = Modifier.size(14.dp),
                )
            }
        } else null,
        modifier = modifier,
    )
}
