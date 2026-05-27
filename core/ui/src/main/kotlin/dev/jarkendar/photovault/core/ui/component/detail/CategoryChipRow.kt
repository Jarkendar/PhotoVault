package dev.jarkendar.photovault.core.ui.component.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.jarkendar.photovault.core.domain.model.Category
import dev.jarkendar.photovault.core.ui.R
import dev.jarkendar.photovault.core.ui.preview.PhonePreview
import dev.jarkendar.photovault.core.ui.preview.previewCategories
import dev.jarkendar.photovault.core.ui.theme.PhotoVaultTheme
import dev.jarkendar.photovault.core.ui.util.parseHexColor

@Composable
fun CategoryChipRow(
    categories: List<Category>,
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
    ) {
        items(categories, key = { it.id.value }) { category ->
            SuggestionChip(
                onClick = {},
                label = { Text(category.name) },
                icon = {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(parseHexColor(category.colorHex)),
                    )
                },
            )
        }
        item {
            SuggestionChip(
                onClick = onAddClick,
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

@PhonePreview
@Composable
private fun CategoryChipRowPreview() {
    PhotoVaultTheme {
        CategoryChipRow(categories = previewCategories(), onAddClick = {})
    }
}
