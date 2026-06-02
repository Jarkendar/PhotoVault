package dev.jskrzypczak.photovault.feature.manage.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DragIndicator
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.jskrzypczak.photovault.core.ui.R
import dev.jskrzypczak.photovault.core.ui.util.parseHexColor
import dev.jskrzypczak.photovault.feature.manage.R as ManageR

/**
 * A single row in the Manage list.
 *
 * @param name Display name of the item.
 * @param colorHex Hex color string (e.g. "#FF8B45"). Null means neutral color (for tags without a color).
 * @param photoCount Number of photos associated with this item.
 * @param onEdit Callback fired when the edit pencil is tapped.
 */
@Composable
internal fun ManageListItem(
    name: String,
    colorHex: String?,
    photoCount: Int,
    onEdit: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val iconColor = if (colorHex != null) parseHexColor(colorHex) else MaterialTheme.colorScheme.onSurfaceVariant
    val bgColor = if (colorHex != null) parseHexColor(colorHex).copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Leading color square with folder icon
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(bgColor),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Default.Folder,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(22.dp),
            )
        }

        Spacer(Modifier.width(16.dp))

        // Name + count
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = name,
                style = MaterialTheme.typography.bodyLarge,
            )
            Text(
                text = pluralStringResource(R.plurals.core_ui_photo_count, photoCount, photoCount),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        // Edit button
        IconButton(onClick = onEdit) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = stringResource(ManageR.string.feature_manage_edit),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp),
            )
        }

        // Drag handle (visual only this etap)
        // TODO(etap-8+): wire ViewModel + repository CRUD (create/rename/delete/reorder)
        Icon(
            imageVector = Icons.Default.DragIndicator,
            contentDescription = stringResource(ManageR.string.feature_manage_reorder),
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .size(20.dp)
                .padding(end = 4.dp),
        )
    }
}
