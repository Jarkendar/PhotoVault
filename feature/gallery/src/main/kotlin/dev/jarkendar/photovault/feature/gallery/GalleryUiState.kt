package dev.jarkendar.photovault.feature.gallery

import dev.jarkendar.photovault.core.domain.id.CategoryId
import dev.jarkendar.photovault.core.domain.model.Category
import dev.jarkendar.photovault.core.domain.model.Photo

sealed interface GalleryUiState {
    data object Loading : GalleryUiState
    data object Empty : GalleryUiState
    data class Error(val message: String) : GalleryUiState
    data class Content(
        val photos: List<Photo>,
        val categories: List<Category>,
        val counts: Map<CategoryId, Int>,
        val selectedCategoryId: CategoryId?,
        val totalCount: Int,
        val currentPage: Int,
        val pages: List<Int>,
    ) : GalleryUiState
}
