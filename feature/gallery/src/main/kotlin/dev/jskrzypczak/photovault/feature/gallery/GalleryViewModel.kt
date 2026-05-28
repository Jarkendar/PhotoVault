package dev.jskrzypczak.photovault.feature.gallery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.jskrzypczak.photovault.core.common.AppDispatchers
import dev.jskrzypczak.photovault.core.domain.id.CategoryId
import dev.jskrzypczak.photovault.core.domain.model.Category
import dev.jskrzypczak.photovault.core.domain.model.Photo
import dev.jskrzypczak.photovault.feature.gallery.domain.usecase.ObserveCategoriesUseCase
import dev.jskrzypczak.photovault.feature.gallery.domain.usecase.ObservePhotosUseCase
import dev.jskrzypczak.photovault.feature.gallery.domain.usecase.RefreshGalleryUseCase
import dev.jskrzypczak.photovault.feature.gallery.domain.usecase.ToggleFavoriteUseCase
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableMap
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class GalleryViewModel(
    private val observePhotosUseCase: ObservePhotosUseCase,
    private val observeCategoriesUseCase: ObserveCategoriesUseCase,
    private val refreshGalleryUseCase: RefreshGalleryUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val dispatchers: AppDispatchers,
) : ViewModel() {

    companion object {
        private const val PAGE_SIZE = 30
    }

    private data class GalleryFilter(
        val selectedCategoryId: CategoryId? = null,
        val searchQuery: String = "",
        val currentPage: Int = 1,
    )

    private val _selectedCategoryId = MutableStateFlow<CategoryId?>(null)
    private val _searchQuery = MutableStateFlow("")
    private val _currentPage = MutableStateFlow(1)

    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val filterFlow: Flow<GalleryFilter> = combine(
        _selectedCategoryId,
        _searchQuery,
        _currentPage,
    ) { sel, q, page -> GalleryFilter(sel, q, page) }

    val uiState: StateFlow<GalleryUiState> = combine(
        observePhotosUseCase(),
        observeCategoriesUseCase(),
        filterFlow,
    ) { photos, categories, filter ->
        buildUiState(photos, categories, filter)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = GalleryUiState.Loading,
    )

    init {
        viewModelScope.launch(dispatchers.io) {
            refreshGalleryUseCase()
        }
    }

    fun onCategorySelect(categoryId: CategoryId?) {
        _selectedCategoryId.value = categoryId
        _currentPage.value = 1
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
        _currentPage.value = 1
    }

    fun onPageClick(page: Int) {
        _currentPage.value = page
    }

    fun onFavoriteClick(photo: Photo) {
        viewModelScope.launch(dispatchers.io) {
            toggleFavoriteUseCase(photo.id)
        }
    }

    fun onRefresh() {
        viewModelScope.launch(dispatchers.io) {
            refreshGalleryUseCase()
        }
    }

    private fun buildUiState(
        photos: List<Photo>,
        categories: List<Category>,
        filter: GalleryFilter,
    ): GalleryUiState {
        val filtered = photos.filter { photo ->
            val matchesCategory = filter.selectedCategoryId == null ||
                photo.categories.any { it.id == filter.selectedCategoryId }
            val matchesQuery = filter.searchQuery.isBlank() ||
                photo.name.contains(filter.searchQuery, ignoreCase = true)
            matchesCategory && matchesQuery
        }

        if (filtered.isEmpty()) return GalleryUiState.Empty

        val counts = photos
            .flatMap { it.categories }
            .groupBy { it.id }
            .mapValues { (_, cats) -> cats.size }
            .toImmutableMap()

        val totalCount = filtered.size
        val pageCount = (totalCount + PAGE_SIZE - 1) / PAGE_SIZE
        val currentPage = filter.currentPage.coerceIn(1, pageCount)
        val pages = (1..pageCount).toImmutableList()
        val pagePhotos = filtered
            .drop((currentPage - 1) * PAGE_SIZE)
            .take(PAGE_SIZE)
            .toImmutableList()

        return GalleryUiState.Content(
            photos = pagePhotos,
            categories = categories.toImmutableList(),
            counts = counts,
            selectedCategoryId = filter.selectedCategoryId,
            totalCount = totalCount,
            currentPage = currentPage,
            pages = pages,
        )
    }
}
