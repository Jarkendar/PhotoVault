package dev.jskrzypczak.photovault.feature.gallery

import dev.jskrzypczak.photovault.core.domain.id.CategoryId
import dev.jskrzypczak.photovault.core.domain.model.Category
import dev.jskrzypczak.photovault.core.domain.model.Photo
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap

sealed interface GalleryUiState {
    data object Loading : GalleryUiState
    data object Empty : GalleryUiState
    data class Error(val message: String) : GalleryUiState
    data class Content(
        val photos: ImmutableList<Photo>,
        val categories: ImmutableList<Category>,
        val counts: ImmutableMap<CategoryId, Int>,
        val selectedCategoryId: CategoryId?,
        val totalCount: Int,
        val currentPage: Int,
        val pages: ImmutableList<Int>,
    ) : GalleryUiState
}
