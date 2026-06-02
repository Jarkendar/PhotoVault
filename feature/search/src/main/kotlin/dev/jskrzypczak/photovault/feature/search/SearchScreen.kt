package dev.jskrzypczak.photovault.feature.search

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.MultiChoiceSegmentedButtonRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.jskrzypczak.photovault.core.domain.id.CategoryId
import dev.jskrzypczak.photovault.core.domain.id.LabelId
import dev.jskrzypczak.photovault.core.domain.id.TagId
import dev.jskrzypczak.photovault.core.domain.model.Category
import dev.jskrzypczak.photovault.core.domain.model.Label
import dev.jskrzypczak.photovault.core.domain.model.Photo
import dev.jskrzypczak.photovault.core.domain.model.Tag
import dev.jskrzypczak.photovault.core.domain.query.MatchMode
import dev.jskrzypczak.photovault.core.ui.component.TagChip
import dev.jskrzypczak.photovault.core.ui.component.gallery.AppBottomNavBar
import dev.jskrzypczak.photovault.core.ui.component.gallery.GalleryDestination
import dev.jskrzypczak.photovault.core.ui.component.gallery.StaggeredPhotoGrid
import dev.jskrzypczak.photovault.core.ui.preview.PhonePreview
import dev.jskrzypczak.photovault.core.ui.state.EmptyState
import dev.jskrzypczak.photovault.core.ui.state.ErrorState
import dev.jskrzypczak.photovault.core.ui.state.LoadingState
import dev.jskrzypczak.photovault.core.ui.theme.PhotoVaultTheme
import dev.jskrzypczak.photovault.core.ui.util.parseHexColor
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

// ─────────────────────────────────────────────────────────────────────────────
// Screen
// ─────────────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    state: SearchUiState,
    filterPanelState: FilterPanelState,
    searchText: String,
    onSearchTextChange: (String) -> Unit = {},
    onBack: () -> Unit = {},
    onToggleCategory: (CategoryId) -> Unit = {},
    onToggleTag: (TagId) -> Unit = {},
    onToggleLabel: (LabelId) -> Unit = {},
    onMatchModeChange: (MatchMode) -> Unit = {},
    onClearFilters: () -> Unit = {},
    onApplyFilters: () -> Unit = {},
    onPhotoClick: (Photo) -> Unit = {},
    onFavoriteClick: (Photo) -> Unit = {},
    onDestinationSelect: (GalleryDestination) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    var filterSheetVisible by rememberSaveable { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Scaffold(
        modifier = modifier,
        topBar = {
            SearchTopBar(
                query = searchText,
                onQueryChange = onSearchTextChange,
                onBack = onBack,
            )
        },
        bottomBar = {
            AppBottomNavBar(
                selectedDestination = GalleryDestination.SEARCH,
                onSelect = onDestinationSelect,
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            // ── Suggestions ──────────────────────────────────────────────
            if (filterPanelState.suggestions.isNotEmpty()) {
                SuggestionsSection(
                    suggestions = filterPanelState.suggestions,
                    onSuggestionClick = { tag -> onSearchTextChange(tag.name) },
                )
                HorizontalDivider()
            }

            // ── Main content area ─────────────────────────────────────────
            when (state) {
                SearchUiState.Idle -> {
                    // Empty area with filter button visible once suggestions are shown
                }

                SearchUiState.Loading -> LoadingState(modifier = Modifier.fillMaxSize())

                SearchUiState.Empty -> {
                    ResultsHeader(
                        count = 0,
                        onFilterClick = { filterSheetVisible = true },
                    )
                    EmptyState(
                        message = stringResource(R.string.feature_search_empty_message),
                        modifier = Modifier.fillMaxSize(),
                    )
                }

                is SearchUiState.Error -> ErrorState(
                    message = state.message,
                    modifier = Modifier.fillMaxSize(),
                )

                is SearchUiState.Content -> {
                    ResultsHeader(
                        count = state.resultCount,
                        onFilterClick = { filterSheetVisible = true },
                    )
                    StaggeredPhotoGrid(
                        photos = state.photos,
                        onPhotoClick = onPhotoClick,
                        onFavoriteClick = onFavoriteClick,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            }
        }
    }

    // ── Filter bottom sheet ──────────────────────────────────────────────────
    if (filterSheetVisible) {
        ModalBottomSheet(
            onDismissRequest = { filterSheetVisible = false },
            sheetState = sheetState,
        ) {
            FilterSheet(
                filterPanelState = filterPanelState,
                onToggleCategory = onToggleCategory,
                onToggleTag = onToggleTag,
                onToggleLabel = onToggleLabel,
                onMatchModeChange = onMatchModeChange,
                onClear = onClearFilters,
                onApply = {
                    onApplyFilters()
                    filterSheetVisible = false
                },
                onDismiss = { filterSheetVisible = false },
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Sub-composables
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun SuggestionsSection(
    suggestions: ImmutableList<Tag>,
    onSuggestionClick: (Tag) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.feature_search_suggestions_header),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        )
        suggestions.forEach { tag ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSuggestionClick(tag) }
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "#",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
                Spacer(Modifier.width(8.dp))
                Column {
                    Text(text = tag.name, style = MaterialTheme.typography.bodyMedium)
                    Text(
                        text = stringResource(R.string.feature_search_tag_subtitle),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@Composable
private fun ResultsHeader(
    count: Int,
    onFilterClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = pluralStringResource(R.plurals.feature_search_photo_count, count, count)
                .uppercase(),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        TextButton(onClick = onFilterClick) {
            Icon(imageVector = Icons.Default.FilterList, contentDescription = null)
            Spacer(Modifier.width(4.dp))
            Text(stringResource(R.string.feature_search_filter_by))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun FilterSheet(
    filterPanelState: FilterPanelState,
    onToggleCategory: (CategoryId) -> Unit,
    onToggleTag: (TagId) -> Unit,
    onToggleLabel: (LabelId) -> Unit,
    onMatchModeChange: (MatchMode) -> Unit,
    onClear: () -> Unit,
    onApply: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {

        // ── Sheet header ─────────────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TextButton(onClick = onDismiss) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                )
            }
            Text(
                text = stringResource(R.string.feature_search_filter_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
            TextButton(onClick = onClear) {
                Text(stringResource(R.string.feature_search_filter_clear))
            }
        }

        HorizontalDivider()

        // ── Scrollable filter content ─────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f, fill = false)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
        ) {

            Spacer(Modifier.height(12.dp))

            // ── AND / OR toggle ───────────────────────────────────────────────
            val draftQuery = filterPanelState.draftQuery
            MultiChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                SegmentedButton(
                    shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
                    onCheckedChange = { onMatchModeChange(MatchMode.ALL) },
                    checked = draftQuery.matchMode == MatchMode.ALL,
                    label = { Text(stringResource(R.string.feature_search_match_all)) },
                )
                SegmentedButton(
                    shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
                    onCheckedChange = { onMatchModeChange(MatchMode.ANY) },
                    checked = draftQuery.matchMode == MatchMode.ANY,
                    label = { Text(stringResource(R.string.feature_search_match_any)) },
                )
            }

            Spacer(Modifier.height(16.dp))

            // ── Categories ────────────────────────────────────────────────────
            if (filterPanelState.availableCategories.isNotEmpty()) {
                FilterSectionHeader(stringResource(R.string.feature_search_categories_header))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    filterPanelState.availableCategories.forEach { category ->
                        val selected = category.id in draftQuery.categoryIds
                        FilterChip(
                            selected = selected,
                            onClick = { onToggleCategory(category.id) },
                            label = { Text("${category.name} ${category.photoCount}") },
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
                Spacer(Modifier.height(16.dp))
            }

            // ── Tags ──────────────────────────────────────────────────────────
            if (filterPanelState.availableTags.isNotEmpty()) {
                FilterSectionHeader(stringResource(R.string.feature_search_tags_header))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    filterPanelState.availableTags.forEach { tag ->
                        TagChip(
                            tag = tag,
                            selected = tag.id in draftQuery.tagIds,
                            onClick = { onToggleTag(tag.id) },
                        )
                    }
                }
                Spacer(Modifier.height(16.dp))
            }

            // ── Labels ────────────────────────────────────────────────────────
            if (filterPanelState.availableLabels.isNotEmpty()) {
                FilterSectionHeader(stringResource(R.string.feature_search_labels_header))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.padding(vertical = 8.dp),
                ) {
                    filterPanelState.availableLabels.forEach { label ->
                        LabelColorDot(
                            label = label,
                            selected = label.id in draftQuery.labelIds,
                            onClick = { onToggleLabel(label.id) },
                        )
                    }
                }
                Spacer(Modifier.height(16.dp))
            }

            // ── Date range (placeholder for now — input UX TBD) ───────────────
            FilterSectionHeader(stringResource(R.string.feature_search_date_range_header))
            Text(
                text = "—",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(vertical = 4.dp),
            )

            Spacer(Modifier.height(24.dp))
        }

        HorizontalDivider()

        // ── Footer: live count + Apply ─────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (filterPanelState.isCounting) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.feature_search_counting),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            } else {
                val count = filterPanelState.liveCount ?: 0
                Text(
                    text = pluralStringResource(R.plurals.feature_search_photo_count, count, count),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }

            Button(onClick = onApply) {
                Icon(imageVector = Icons.Default.FilterList, contentDescription = null)
                Spacer(Modifier.width(4.dp))
                Text(stringResource(R.string.feature_search_filter_apply))
            }
        }
    }
}

@Composable
private fun FilterSectionHeader(title: String, modifier: Modifier = Modifier) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.SemiBold,
        modifier = modifier.padding(bottom = 8.dp),
    )
}

@Composable
private fun LabelColorDot(
    label: Label,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val color = parseHexColor(label.colorHex)
    Box(
        modifier = modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(color)
            .then(
                if (selected) Modifier.border(3.dp, MaterialTheme.colorScheme.primary, CircleShape)
                else Modifier,
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        if (selected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(18.dp),
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Previews
// ─────────────────────────────────────────────────────────────────────────────

@PhonePreview
@Composable
private fun SearchScreenIdlePreview() {
    PhotoVaultTheme(dynamicColor = false) {
        SearchScreen(
            state = SearchUiState.Idle,
            filterPanelState = FilterPanelState(),
            searchText = "",
        )
    }
}

@PhonePreview
@Composable
private fun SearchScreenSuggestionsPreview() {
    PhotoVaultTheme(dynamicColor = false) {
        SearchScreen(
            state = SearchUiState.Idle,
            filterPanelState = previewFilterPanelWithSuggestions(),
            searchText = "morze",
        )
    }
}

@PhonePreview
@Composable
private fun SearchScreenLoadingPreview() {
    PhotoVaultTheme(dynamicColor = false) {
        SearchScreen(
            state = SearchUiState.Loading,
            filterPanelState = FilterPanelState(),
            searchText = "morze",
        )
    }
}

@PhonePreview
@Composable
private fun SearchScreenEmptyPreview() {
    PhotoVaultTheme(dynamicColor = false) {
        SearchScreen(
            state = SearchUiState.Empty,
            filterPanelState = FilterPanelState(),
            searchText = "xyzzy",
        )
    }
}

@PhonePreview
@Composable
private fun SearchScreenContentPreview() {
    PhotoVaultTheme(dynamicColor = false) {
        SearchScreen(
            state = previewContentState(),
            filterPanelState = FilterPanelState(),
            searchText = "morze",
        )
    }
}

// ─── Preview fixtures ─────────────────────────────────────────────────────────

private fun previewContentState(): SearchUiState.Content {
    val photos = buildPreviewPhotos()
    return SearchUiState.Content(photos = photos, resultCount = photos.size)
}

private fun buildPreviewPhotos(): kotlinx.collections.immutable.ImmutableList<Photo> {
    val emptyTags = persistentListOf<Tag>()
    val emptyCategories = persistentListOf<Category>()
    val emptyLabels = persistentListOf<Label>()
    return List(6) { i ->
        Photo(
            id = dev.jskrzypczak.photovault.core.domain.id.PhotoId("p$i"),
            name = "photo_$i.jpg",
            sizeBytes = 1_024_000L,
            mimeType = "image/jpeg",
            width = 800 + i * 50,
            height = 600 + i * 30,
            capturedAt = null,
            uploadedAt = kotlin.time.Instant.fromEpochMilliseconds(0L),
            camera = null,
            location = null,
            tags = emptyTags,
            categories = emptyCategories,
            labels = emptyLabels,
            isFavorite = i % 2 == 0,
        )
    }.toImmutableList()
}

private fun previewFilterPanelWithSuggestions(): FilterPanelState {
    val morze = Tag(
        id = dev.jskrzypczak.photovault.core.domain.id.TagId("tag-001"),
        name = "#morze",
        photoCount = 48,
    )
    return FilterPanelState(suggestions = persistentListOf(morze))
}
