package dev.jskrzypczak.photovault.core.ui.component.detail

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
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.outlined.HourglassEmpty
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.jskrzypczak.photovault.core.domain.model.Category
import dev.jskrzypczak.photovault.core.ui.R
import dev.jskrzypczak.photovault.core.ui.preview.PhonePreview
import dev.jskrzypczak.photovault.core.ui.preview.previewCategories
import dev.jskrzypczak.photovault.core.ui.theme.PhotoVaultTheme
import dev.jskrzypczak.photovault.core.ui.util.parseHexColor
import kotlinx.collections.immutable.ImmutableList

@Composable
fun CategoryChipRow(
    categories: ImmutableList<Category>,
    onAddClick: () -> Unit,
    onToggleAutoEnabled: (Category) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
    ) {
        items(categories, key = { it.id.value }) { category ->
            FilterChip(
                selected = category.autoEnabled,
                onClick = { onToggleAutoEnabled(category) },
                label = { Text(category.name) },
                leadingIcon = {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(parseHexColor(category.colorHex)),
                    )
                },
                trailingIcon = when {
                    category.autoEnabled -> {
                        {
                            Icon(
                                imageVector = Icons.Default.SmartToy,
                                contentDescription = stringResource(R.string.core_ui_auto_tag_enabled),
                                modifier = Modifier.size(16.dp),
                            )
                        }
                    }
                    !category.rolledOut -> {
                        {
                            Icon(
                                imageVector = Icons.Outlined.HourglassEmpty,
                                contentDescription = stringResource(R.string.core_ui_tag_processing),
                                modifier = Modifier.size(14.dp),
                            )
                        }
                    }
                    else -> null
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
