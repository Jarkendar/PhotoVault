package dev.jarkendar.photovault.core.ui.component

import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.jarkendar.photovault.core.domain.model.Tag

@Composable
fun TagChip(
    tag: Tag,
    selected: Boolean = false,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(text = tag.name) },
        modifier = modifier,
    )
}
