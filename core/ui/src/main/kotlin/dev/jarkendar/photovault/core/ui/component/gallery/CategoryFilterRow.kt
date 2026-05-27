package dev.jarkendar.photovault.core.ui.component.gallery

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.jarkendar.photovault.core.domain.id.CategoryId
import dev.jarkendar.photovault.core.domain.model.Category
import dev.jarkendar.photovault.core.ui.R
import dev.jarkendar.photovault.core.ui.preview.PhonePreview
import dev.jarkendar.photovault.core.ui.preview.previewCategories
import dev.jarkendar.photovault.core.ui.theme.PhotoVaultTheme

@Composable
fun CategoryFilterRow(
    categories: List<Category>,
    counts: Map<CategoryId, Int>,
    selectedCategoryId: CategoryId?,
    onCategorySelect: (CategoryId?) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
    ) {
        item {
            val allSelected = selectedCategoryId == null
            FilterChip(
                selected = allSelected,
                onClick = { onCategorySelect(null) },
                label = { Text(stringResource(R.string.core_ui_all_categories)) },
                leadingIcon = if (allSelected) {
                    {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                        )
                    }
                } else null,
            )
        }
        items(categories, key = { it.id.value }) { category ->
            val count = counts[category.id] ?: 0
            val selected = selectedCategoryId == category.id
            FilterChip(
                selected = selected,
                onClick = { onCategorySelect(category.id) },
                label = { Text("${category.name} · $count") },
                leadingIcon = if (selected) {
                    {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                        )
                    }
                } else null,
            )
        }
    }
}

@PhonePreview
@Composable
private fun CategoryFilterRowAllSelectedPreview() {
    PhotoVaultTheme {
        CategoryFilterRow(
            categories = previewCategories(),
            counts = mapOf(),
            selectedCategoryId = null,
            onCategorySelect = {},
        )
    }
}

@PhonePreview
@Composable
private fun CategoryFilterRowCategorySelectedPreview() {
    val categories = previewCategories()
    PhotoVaultTheme {
        CategoryFilterRow(
            categories = categories,
            counts = mapOf(categories[0].id to 48, categories[1].id to 73),
            selectedCategoryId = categories[0].id,
            onCategorySelect = {},
        )
    }
}
