package dev.jskrzypczak.photovault.feature.manage

import dev.jskrzypczak.photovault.core.domain.model.Category
import dev.jskrzypczak.photovault.core.domain.model.Label
import dev.jskrzypczak.photovault.core.domain.model.Tag
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

enum class ManageTab { CATEGORIES, TAGS, LABELS }

sealed interface ManageUiState {

    data object Loading : ManageUiState

    data object Empty : ManageUiState

    data class Error(val message: String) : ManageUiState

    data class Content(
        val selectedTab: ManageTab = ManageTab.CATEGORIES,
        val categories: ImmutableList<Category> = persistentListOf(),
        val tags: ImmutableList<Tag> = persistentListOf(),
        val labels: ImmutableList<Label> = persistentListOf(),
    ) : ManageUiState
}
