package dev.jskrzypczak.photovault.feature.manage

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.jskrzypczak.photovault.core.domain.model.Category
import dev.jskrzypczak.photovault.core.domain.model.Label
import dev.jskrzypczak.photovault.core.domain.model.Tag
import dev.jskrzypczak.photovault.core.ui.component.gallery.AppBottomNavBar
import dev.jskrzypczak.photovault.core.ui.component.gallery.GalleryDestination
import dev.jskrzypczak.photovault.core.ui.preview.PhonePreview
import dev.jskrzypczak.photovault.core.ui.state.EmptyState
import dev.jskrzypczak.photovault.core.ui.state.ErrorState
import dev.jskrzypczak.photovault.core.ui.state.LoadingState
import dev.jskrzypczak.photovault.core.ui.theme.PhotoVaultTheme
import dev.jskrzypczak.photovault.feature.manage.component.ManageListItem
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

// ─────────────────────────────────────────────────────────────────────────────
// Screen
// ─────────────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageScreen(
    state: ManageUiState,
    onBack: () -> Unit = {},
    onSearchClick: () -> Unit = {},
    onTabSelect: (ManageTab) -> Unit = {},
    onEditItem: (Any) -> Unit = {},
    onAddClick: () -> Unit = {},
    onDestinationSelect: (GalleryDestination) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val selectedTab = if (state is ManageUiState.Content) state.selectedTab else ManageTab.CATEGORIES

    val fabLabel = when (selectedTab) {
        ManageTab.CATEGORIES -> stringResource(R.string.feature_manage_add_category)
        ManageTab.TAGS -> stringResource(R.string.feature_manage_add_tag)
        ManageTab.LABELS -> stringResource(R.string.feature_manage_add_label)
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.feature_manage_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.feature_manage_back),
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onSearchClick) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = stringResource(R.string.feature_manage_search),
                        )
                    }
                },
            )
        },
        bottomBar = {
            AppBottomNavBar(
                selectedDestination = GalleryDestination.MANAGE,
                onSelect = onDestinationSelect,
            )
        },
        floatingActionButton = {
            // TODO(etap-8+): wire ViewModel + repository CRUD (create/rename/delete/reorder);
            // labels need backend (SERVER_TODO).
            ExtendedFloatingActionButton(
                onClick = onAddClick,
                text = { Text(fabLabel) },
                icon = {},
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            // ── Tab row ──────────────────────────────────────────────────────
            val tabs = ManageTab.entries
            SingleChoiceSegmentedButtonRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
            ) {
                tabs.forEachIndexed { index, tab ->
                    SegmentedButton(
                        shape = SegmentedButtonDefaults.itemShape(index = index, count = tabs.size),
                        onClick = { onTabSelect(tab) },
                        selected = tab == selectedTab,
                        label = {
                            Text(
                                when (tab) {
                                    ManageTab.CATEGORIES -> stringResource(R.string.feature_manage_tab_categories)
                                    ManageTab.TAGS -> stringResource(R.string.feature_manage_tab_tags)
                                    ManageTab.LABELS -> stringResource(R.string.feature_manage_tab_labels)
                                }
                            )
                        },
                    )
                }
            }

            // ── Body ─────────────────────────────────────────────────────────
            when (state) {
                ManageUiState.Loading -> LoadingState(modifier = Modifier.fillMaxSize())

                ManageUiState.Empty -> EmptyState(
                    message = stringResource(R.string.feature_manage_empty),
                    modifier = Modifier.fillMaxSize(),
                )

                is ManageUiState.Error -> ErrorState(
                    message = state.message,
                    modifier = Modifier.fillMaxSize(),
                )

                is ManageUiState.Content -> ManageContent(
                    state = state,
                    onEditItem = onEditItem,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Sub-composables
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun ManageContent(
    state: ManageUiState.Content,
    onEditItem: (Any) -> Unit,
    modifier: Modifier = Modifier,
) {
    when (state.selectedTab) {
        ManageTab.CATEGORIES -> {
            if (state.categories.isEmpty()) {
                EmptyState(
                    message = stringResource(R.string.feature_manage_empty),
                    modifier = modifier,
                )
            } else {
                LazyColumn(modifier = modifier) {
                    items(state.categories, key = { it.id.value }) { category ->
                        ManageListItem(
                            name = category.name,
                            colorHex = category.colorHex,
                            photoCount = category.photoCount,
                            onEdit = { onEditItem(category.id) },
                        )
                    }
                }
            }
        }

        ManageTab.TAGS -> {
            if (state.tags.isEmpty()) {
                EmptyState(
                    message = stringResource(R.string.feature_manage_empty),
                    modifier = modifier,
                )
            } else {
                LazyColumn(modifier = modifier) {
                    items(state.tags, key = { it.id.value }) { tag ->
                        ManageListItem(
                            name = tag.name,
                            colorHex = null,
                            photoCount = tag.photoCount,
                            onEdit = { onEditItem(tag.id) },
                        )
                    }
                }
            }
        }

        ManageTab.LABELS -> {
            if (state.labels.isEmpty()) {
                EmptyState(
                    message = stringResource(R.string.feature_manage_empty),
                    modifier = modifier,
                )
            } else {
                LazyColumn(modifier = modifier) {
                    items(state.labels, key = { it.id.value }) { label ->
                        ManageListItem(
                            name = label.name,
                            colorHex = label.colorHex,
                            photoCount = label.photoCount,
                            onEdit = { onEditItem(label.id) },
                        )
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Previews
// ─────────────────────────────────────────────────────────────────────────────

@PhonePreview
@Composable
private fun ManageScreenCategoriesPreview() {
    PhotoVaultTheme(dynamicColor = false) {
        ManageScreen(state = previewContentState())
    }
}

@PhonePreview
@Composable
private fun ManageScreenTagsPreview() {
    PhotoVaultTheme(dynamicColor = false) {
        ManageScreen(state = previewContentState(tab = ManageTab.TAGS))
    }
}

@PhonePreview
@Composable
private fun ManageScreenLoadingPreview() {
    PhotoVaultTheme(dynamicColor = false) {
        ManageScreen(state = ManageUiState.Loading)
    }
}

@PhonePreview
@Composable
private fun ManageScreenEmptyPreview() {
    PhotoVaultTheme(dynamicColor = false) {
        ManageScreen(state = ManageUiState.Empty)
    }
}

// ─── Preview fixtures ─────────────────────────────────────────────────────────

private fun previewContentState(tab: ManageTab = ManageTab.CATEGORIES): ManageUiState.Content {
    val categories = listOf(
        buildCategory("c1", "Natura", "#4CAF50", 48),
        buildCategory("c2", "Ludzie", "#E91E63", 73),
        buildCategory("c3", "Podróże", "#2196F3", 124),
        buildCategory("c4", "Jedzenie", "#FF9800", 31),
        buildCategory("c5", "Architektura", "#9C27B0", 56),
        buildCategory("c6", "Zwierzęta", "#9E9E9E", 22),
        buildCategory("c7", "Wydarzenia", "#00BCD4", 19),
    ).toImmutableList()

    val tags = listOf(
        buildTag("t1", "#morze", 48),
        buildTag("t2", "#góry", 35),
        buildTag("t3", "#miasto", 92),
        buildTag("t4", "#rodzina", 67),
    ).toImmutableList()

    return ManageUiState.Content(
        selectedTab = tab,
        categories = categories,
        tags = tags,
        labels = persistentListOf(),
    )
}

private fun buildCategory(id: String, name: String, colorHex: String, photoCount: Int) =
    Category(
        id = dev.jskrzypczak.photovault.core.domain.id.CategoryId(id),
        name = name,
        colorHex = colorHex,
        photoCount = photoCount,
    )

private fun buildTag(id: String, name: String, photoCount: Int) =
    Tag(
        id = dev.jskrzypczak.photovault.core.domain.id.TagId(id),
        name = name,
        photoCount = photoCount,
    )
