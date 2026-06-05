package dev.jskrzypczak.photovault.feature.gallery

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.jskrzypczak.photovault.core.common.AppDispatchers
import dev.jskrzypczak.photovault.core.domain.id.PhotoId
import dev.jskrzypczak.photovault.core.domain.model.Category
import dev.jskrzypczak.photovault.core.domain.model.Tag
import dev.jskrzypczak.photovault.feature.gallery.domain.usecase.ObservePhotoUseCase
import dev.jskrzypczak.photovault.feature.gallery.domain.usecase.ToggleCategoryAutoEnabledUseCase
import dev.jskrzypczak.photovault.feature.gallery.domain.usecase.ToggleFavoriteUseCase
import dev.jskrzypczak.photovault.feature.gallery.domain.usecase.ToggleTagAutoEnabledUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PhotoDetailViewModel(
    savedStateHandle: SavedStateHandle,
    observePhotoUseCase: ObservePhotoUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val toggleTagAutoEnabledUseCase: ToggleTagAutoEnabledUseCase,
    private val toggleCategoryAutoEnabledUseCase: ToggleCategoryAutoEnabledUseCase,
    private val dispatchers: AppDispatchers,
) : ViewModel() {

    private val photoId = PhotoId(checkNotNull(savedStateHandle["photoId"]))

    val uiState: StateFlow<PhotoDetailUiState> = observePhotoUseCase(photoId)
        .map { photo ->
            if (photo == null) PhotoDetailUiState.Error("") else PhotoDetailUiState.Content(photo)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = PhotoDetailUiState.Loading,
        )

    fun onFavoriteToggle() {
        viewModelScope.launch(dispatchers.io) {
            toggleFavoriteUseCase(photoId)
        }
    }

    fun onToggleTagAutoEnabled(tag: Tag) {
        viewModelScope.launch(dispatchers.io) {
            toggleTagAutoEnabledUseCase(tag.id, !tag.autoEnabled)
        }
    }

    fun onToggleCategoryAutoEnabled(category: Category) {
        viewModelScope.launch(dispatchers.io) {
            toggleCategoryAutoEnabledUseCase(category.id, !category.autoEnabled)
        }
    }
}
