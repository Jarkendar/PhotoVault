package dev.jskrzypczak.photovault.feature.gallery

import androidx.annotation.StringRes
import dev.jskrzypczak.photovault.core.domain.id.CategoryId
import dev.jskrzypczak.photovault.core.domain.model.Category
import dev.jskrzypczak.photovault.core.domain.model.Photo
import dev.jskrzypczak.photovault.core.domain.model.ProcessingStatus
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap


sealed interface GalleryUiState {
    data object Loading : GalleryUiState
    data object Empty : GalleryUiState
    data class Error(@StringRes val messageResId: Int) : GalleryUiState
    data class Content(
        val photos: ImmutableList<Photo>,
        val categories: ImmutableList<Category>,
        val counts: ImmutableMap<CategoryId, Int>,
        val selectedCategoryId: CategoryId?,
        val totalCount: Int,
        val currentPage: Int,
        val pages: ImmutableList<Int>,
        /** Number of photos in [ProcessingStatus.PENDING_CATEGORIZATION] state. */
        val pendingCategorizationCount: Int,
        /** Number of photos in [ProcessingStatus.READY] state (fully categorized). */
        val categorizedCount: Int,
    ) : GalleryUiState
}
