package dev.jskrzypczak.photovault.feature.search

import dev.jskrzypczak.photovault.core.domain.model.Category
import dev.jskrzypczak.photovault.core.domain.model.Label
import dev.jskrzypczak.photovault.core.domain.model.Photo
import dev.jskrzypczak.photovault.core.domain.model.Tag
import dev.jskrzypczak.photovault.core.domain.query.MatchMode
import dev.jskrzypczak.photovault.core.domain.query.SearchQuery
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlin.time.Instant

/** Main state of the search screen. */
sealed interface SearchUiState {
    /** Initial state — no query entered yet. Shows suggestions or empty prompt. */
    data object Idle : SearchUiState

    /** Search request is in flight. */
    data object Loading : SearchUiState

    /** Query returned zero results. */
    data object Empty : SearchUiState

    /** Network or unexpected error. */
    data class Error(val message: String) : SearchUiState

    /** Successful search with at least one result. */
    data class Content(
        val photos: ImmutableList<Photo>,
        val resultCount: Int,
    ) : SearchUiState
}

/**
 * State of the filter bottom-sheet and suggestion bar.
 *
 * Separate from [SearchUiState] so it survives screen state transitions
 * (e.g. remains loaded while the main area shows [SearchUiState.Loading]).
 */
data class FilterPanelState(
    /** Draft query being edited in the bottom-sheet (not yet applied). */
    val draftQuery: SearchQuery = SearchQuery(),

    /** Available tags fetched from the repository (with photoCount). */
    val availableTags: ImmutableList<Tag> = persistentListOf(),

    /** Available categories fetched from the repository (with photoCount). */
    val availableCategories: ImmutableList<Category> = persistentListOf(),

    /** Available labels fetched from the repository (with photoCount). */
    val availableLabels: ImmutableList<Label> = persistentListOf(),

    /** Tag name suggestions filtered by the current search text prefix. */
    val suggestions: ImmutableList<Tag> = persistentListOf(),

    /** Live count of photos matching [draftQuery]. Null while the count request is in flight. */
    val liveCount: Int? = null,

    /** True while a count request triggered by draft edits is pending. */
    val isCounting: Boolean = false,
)

// ─── Convenience helpers ────────────────────────────────────────────────────

fun FilterPanelState.toggleCategory(id: dev.jskrzypczak.photovault.core.domain.id.CategoryId): FilterPanelState {
    val current = draftQuery.categoryIds
    val next = if (id in current) current - id else current + id
    return copy(draftQuery = draftQuery.copy(categoryIds = next))
}

fun FilterPanelState.toggleTag(id: dev.jskrzypczak.photovault.core.domain.id.TagId): FilterPanelState {
    val current = draftQuery.tagIds
    val next = if (id in current) current - id else current + id
    return copy(draftQuery = draftQuery.copy(tagIds = next))
}

fun FilterPanelState.toggleLabel(id: dev.jskrzypczak.photovault.core.domain.id.LabelId): FilterPanelState {
    val current = draftQuery.labelIds
    val next = if (id in current) current - id else current + id
    return copy(draftQuery = draftQuery.copy(labelIds = next))
}

fun FilterPanelState.withMatchMode(matchMode: MatchMode): FilterPanelState =
    copy(draftQuery = draftQuery.copy(matchMode = matchMode))

fun FilterPanelState.withDateFrom(dateFrom: Instant?): FilterPanelState =
    copy(draftQuery = draftQuery.copy(dateFrom = dateFrom))

fun FilterPanelState.withDateTo(dateTo: Instant?): FilterPanelState =
    copy(draftQuery = draftQuery.copy(dateTo = dateTo))

fun FilterPanelState.cleared(): FilterPanelState =
    copy(draftQuery = SearchQuery())
