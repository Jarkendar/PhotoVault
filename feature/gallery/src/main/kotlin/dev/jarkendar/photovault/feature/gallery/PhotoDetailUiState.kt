package dev.jarkendar.photovault.feature.gallery

import dev.jarkendar.photovault.core.domain.model.Photo

sealed interface PhotoDetailUiState {
    data object Loading : PhotoDetailUiState
    data class Error(val message: String) : PhotoDetailUiState
    data class Content(val photo: Photo) : PhotoDetailUiState
}
