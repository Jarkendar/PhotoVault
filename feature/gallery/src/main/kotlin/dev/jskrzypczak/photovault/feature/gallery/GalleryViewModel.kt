package dev.jskrzypczak.photovault.feature.gallery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.jskrzypczak.photovault.core.common.AppDispatchers
import dev.jskrzypczak.photovault.core.domain.error.DomainError
import dev.jskrzypczak.photovault.core.domain.id.CategoryId
import dev.jskrzypczak.photovault.core.domain.model.Category
import dev.jskrzypczak.photovault.core.domain.model.Photo
import dev.jskrzypczak.photovault.core.domain.model.ProcessingStatus
import dev.jskrzypczak.photovault.feature.gallery.domain.usecase.ObserveCategoriesUseCase
import dev.jskrzypczak.photovault.feature.gallery.domain.usecase.ObservePhotosUseCase
import dev.jskrzypczak.photovault.feature.gallery.domain.usecase.RefreshGalleryUseCase
import dev.jskrzypczak.photovault.feature.gallery.domain.usecase.ToggleFavoriteUseCase
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableMap
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
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
        /** Interval between background gallery refreshes to pick up categorisation changes. */
        private const val REFRESH_INTERVAL_MS = 5 * 60_000L
    }

    private data class GalleryFilter(
        val selectedCategoryId: CategoryId? = null,
        val searchQuery: String = "",
        val currentPage: Int = 1,
    )

    private val _selectedCategoryId = MutableStateFlow<CategoryId?>(null)
    private val _searchQuery = MutableStateFlow("")
    private val _currentPage = MutableStateFlow(1)
    private val _isRefreshing = MutableStateFlow(false)

    /** Non-null while there is a pending refresh error and Room is empty. */
    private val _refreshError = MutableStateFlow<DomainError?>(null)

    private var periodicRefreshJob: Job? = null

    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val filterFlow: Flow<GalleryFilter> = combine(
        _selectedCategoryId,
        _searchQuery,
        _currentPage,
    ) { sel, q, page -> GalleryFilter(sel, q, page) }

    val uiState: StateFlow<GalleryUiState> = combine(
        observePhotosUseCase(),
        observeCategoriesUseCase(),
        filterFlow,
        _refreshError,
    ) { photos, categories, filter, refreshError ->
        buildUiState(photos, categories, filter, refreshError)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = GalleryUiState.Loading,
    )

    init {
        viewModelScope.launch(dispatchers.io) {
            doRefresh()
        }
    }

    fun onScreenVisible() {
        periodicRefreshJob?.cancel()
        periodicRefreshJob = viewModelScope.launch(dispatchers.io) {
            while (true) {
                delay(REFRESH_INTERVAL_MS)
                doRefresh()
            }
        }
    }

    fun onScreenHidden() {
        periodicRefreshJob?.cancel()
        periodicRefreshJob = null
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
            doRefresh(showSpinner = true)
        }
    }

    fun onAutoRefresh() {
        viewModelScope.launch(dispatchers.io) {
            doRefresh(showSpinner = false)
        }
    }

    private suspend fun doRefresh(showSpinner: Boolean = false) {
        if (showSpinner) _isRefreshing.value = true
        refreshGalleryUseCase()
            .onSuccess { _refreshError.value = null }
            .onFailure { e ->
                _refreshError.value = when (e) {
                    is DomainError -> e
                    else -> DomainError.Unknown(e)
                }
            }
        if (showSpinner) _isRefreshing.value = false
    }

    private fun buildUiState(
        photos: List<Photo>,
        categories: List<Category>,
        filter: GalleryFilter,
        refreshError: DomainError?,
    ): GalleryUiState {
        val filtered = photos.filter { photo ->
            val matchesCategory = filter.selectedCategoryId == null ||
                photo.categories.any { it.id == filter.selectedCategoryId }
            val matchesQuery = filter.searchQuery.isBlank() ||
                photo.name.contains(filter.searchQuery, ignoreCase = true)
            matchesCategory && matchesQuery
        }

        // Show error state only when Room is empty and a refresh error is pending.
        // If Room already has cached photos keep showing them (offline-first).
        if (filtered.isEmpty()) {
            if (refreshError != null) return GalleryUiState.Error(refreshError.toMessageResId())
            return GalleryUiState.Empty
        }

        val counts = photos
            .flatMap { it.categories }
            .groupBy { it.id }
            .mapValues { (_, cats) -> cats.size }
            .toImmutableMap()

        val pendingCategorizationCount = photos.count {
            it.processingStatus == ProcessingStatus.PENDING_CATEGORIZATION
        }
        val categorizedCount = photos.count {
            it.processingStatus == ProcessingStatus.READY
        }

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
            pendingCategorizationCount = pendingCategorizationCount,
            categorizedCount = categorizedCount,
        )
    }

    private fun DomainError.toMessageResId(): Int = when (this) {
        DomainError.NoConnectivity -> R.string.feature_gallery_error_no_connectivity
        DomainError.Unauthenticated -> R.string.feature_gallery_error_unauthenticated
        is DomainError.ServerError -> R.string.feature_gallery_error_server
        else -> R.string.feature_gallery_error_unknown
    }
}
