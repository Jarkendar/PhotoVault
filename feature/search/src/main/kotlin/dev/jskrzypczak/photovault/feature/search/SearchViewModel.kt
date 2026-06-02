package dev.jskrzypczak.photovault.feature.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.jskrzypczak.photovault.core.common.AppDispatchers
import dev.jskrzypczak.photovault.core.domain.id.CategoryId
import dev.jskrzypczak.photovault.core.domain.id.LabelId
import dev.jskrzypczak.photovault.core.domain.id.TagId
import dev.jskrzypczak.photovault.core.domain.query.MatchMode
import dev.jskrzypczak.photovault.core.domain.query.SearchQuery
import dev.jskrzypczak.photovault.feature.search.domain.usecase.CountPhotosUseCase
import dev.jskrzypczak.photovault.feature.search.domain.usecase.ObserveCategoriesUseCase
import dev.jskrzypczak.photovault.feature.search.domain.usecase.ObserveLabelsUseCase
import dev.jskrzypczak.photovault.feature.search.domain.usecase.ObserveTagsUseCase
import dev.jskrzypczak.photovault.feature.search.domain.usecase.SearchPhotosUseCase
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Instant

@OptIn(FlowPreview::class)
class SearchViewModel(
    private val searchPhotosUseCase: SearchPhotosUseCase,
    private val countPhotosUseCase: CountPhotosUseCase,
    private val observeTagsUseCase: ObserveTagsUseCase,
    private val observeCategoriesUseCase: ObserveCategoriesUseCase,
    private val observeLabelsUseCase: ObserveLabelsUseCase,
    private val dispatchers: AppDispatchers,
) : ViewModel() {

    companion object {
        private const val SEARCH_DEBOUNCE_MS = 400L
        private const val COUNT_DEBOUNCE_MS = 300L
        private const val SUGGESTION_LIMIT = 5
    }

    // ─── Publicly exposed state ──────────────────────────────────────────────

    private val _uiState = MutableStateFlow<SearchUiState>(SearchUiState.Idle)
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private val _filterPanelState = MutableStateFlow(FilterPanelState())
    val filterPanelState: StateFlow<FilterPanelState> = _filterPanelState.asStateFlow()

    private val _searchText = MutableStateFlow("")
    val searchText: StateFlow<String> = _searchText.asStateFlow()

    // Tracks the filters that were last "applied" (not the draft in the sheet).
    // Text is kept separately in _searchText.
    private val _appliedFilters = MutableStateFlow(SearchQuery())

    // ─── Init ───────────────────────────────────────────────────────────────

    init {
        observeTagsAndCategories()
        observeTextChanges()
        observeDraftQueryForCount()
    }

    // ─── Event handlers ──────────────────────────────────────────────────────

    fun onSearchTextChange(text: String) {
        _searchText.value = text
        updateSuggestions(text)
    }

    fun onToggleCategory(id: CategoryId) {
        _filterPanelState.update { it.toggleCategory(id) }
    }

    fun onToggleTag(id: TagId) {
        _filterPanelState.update { it.toggleTag(id) }
    }

    fun onToggleLabel(id: LabelId) {
        _filterPanelState.update { it.toggleLabel(id) }
    }

    fun onMatchModeChange(matchMode: MatchMode) {
        _filterPanelState.update { it.withMatchMode(matchMode) }
    }

    fun onDateFromChange(dateFrom: Instant?) {
        _filterPanelState.update { it.withDateFrom(dateFrom) }
    }

    fun onDateToChange(dateTo: Instant?) {
        _filterPanelState.update { it.withDateTo(dateTo) }
    }

    /** Reset all draft filters to their defaults. */
    fun onClearFilters() {
        _filterPanelState.update { it.cleared() }
    }

    /**
     * Apply the current draft filters and trigger a search combining them
     * with the current search text.
     */
    fun onApplyFilters() {
        val draft = _filterPanelState.value.draftQuery
        _appliedFilters.value = draft
        triggerSearch(_searchText.value, draft)
    }

    // ─── Private helpers ─────────────────────────────────────────────────────

    private fun observeTagsAndCategories() {
        viewModelScope.launch(dispatchers.default) {
            observeTagsUseCase().collect { tags ->
                _filterPanelState.update { state ->
                    state.copy(
                        availableTags = tags.toImmutableList(),
                        suggestions = buildSuggestions(_searchText.value, tags),
                    )
                }
            }
        }
        viewModelScope.launch(dispatchers.default) {
            observeCategoriesUseCase().collect { cats ->
                _filterPanelState.update { it.copy(availableCategories = cats.toImmutableList()) }
            }
        }
        viewModelScope.launch(dispatchers.default) {
            observeLabelsUseCase().collect { labels ->
                _filterPanelState.update { it.copy(availableLabels = labels.toImmutableList()) }
            }
        }
    }

    private fun observeTextChanges() {
        viewModelScope.launch {
            _searchText
                // Skip first emission (initial empty string → Idle, not Loading)
                .drop(1)
                .debounce(SEARCH_DEBOUNCE_MS)
                .collect { text ->
                    if (text.isBlank()) {
                        _uiState.value = SearchUiState.Idle
                    } else {
                        triggerSearch(text, _appliedFilters.value)
                    }
                }
        }
    }

    private fun observeDraftQueryForCount() {
        viewModelScope.launch {
            _filterPanelState
                .map { it.draftQuery }
                .distinctUntilChanged()
                .debounce(COUNT_DEBOUNCE_MS)
                .collect { draft ->
                    _filterPanelState.update { it.copy(isCounting = true) }
                    val count = countPhotosUseCase(
                        draft.copy(text = _searchText.value),
                    ).getOrNull()
                    _filterPanelState.update { it.copy(liveCount = count, isCounting = false) }
                }
        }
    }

    private fun triggerSearch(text: String, filters: SearchQuery) {
        viewModelScope.launch(dispatchers.io) {
            _uiState.value = SearchUiState.Loading
            val query = filters.copy(text = text)
            searchPhotosUseCase(query).fold(
                onSuccess = { photos ->
                    _uiState.value = if (photos.isEmpty()) {
                        SearchUiState.Empty
                    } else {
                        SearchUiState.Content(
                            photos = photos.toImmutableList(),
                            resultCount = photos.size,
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.value = SearchUiState.Error(error.message ?: "")
                },
            )
        }
    }

    private fun updateSuggestions(text: String) {
        _filterPanelState.update { state ->
            state.copy(suggestions = buildSuggestions(text, state.availableTags))
        }
    }

    private fun buildSuggestions(
        text: String,
        tags: List<dev.jskrzypczak.photovault.core.domain.model.Tag>,
    ) = if (text.isBlank()) {
        persistentListOf<dev.jskrzypczak.photovault.core.domain.model.Tag>()
    } else {
        tags
            .filter { tag -> tag.name.contains(text, ignoreCase = true) }
            .take(SUGGESTION_LIMIT)
            .toImmutableList()
    }
}
