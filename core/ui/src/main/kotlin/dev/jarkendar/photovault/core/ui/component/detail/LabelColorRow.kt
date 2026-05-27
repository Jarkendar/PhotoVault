package dev.jarkendar.photovault.core.ui.component.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import dev.jarkendar.photovault.core.domain.model.Label
import dev.jarkendar.photovault.core.ui.preview.PhonePreview
import dev.jarkendar.photovault.core.ui.preview.previewLabels
import dev.jarkendar.photovault.core.ui.theme.PhotoVaultTheme
import dev.jarkendar.photovault.core.ui.util.parseHexColor

@Composable
fun LabelColorRow(
    labels: List<Label>,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        labels.forEach { label ->
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(parseHexColor(label.colorHex)),
            )
        }
    }
}

@PhonePreview
@Composable
private fun LabelColorRowPreview() {
    PhotoVaultTheme {
        LabelColorRow(labels = previewLabels())
    }
}
