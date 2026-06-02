package dev.jskrzypczak.photovault.core.domain.query

import dev.jskrzypczak.photovault.core.domain.id.CategoryId
import dev.jskrzypczak.photovault.core.domain.id.LabelId
import dev.jskrzypczak.photovault.core.domain.id.TagId
import kotlin.time.Instant

data class SearchQuery(
    val text: String = "",
    val tagIds: Set<TagId> = emptySet(),
    val categoryIds: Set<CategoryId> = emptySet(),
    val labelIds: Set<LabelId> = emptySet(),
    val favoritesOnly: Boolean = false,
    val matchMode: MatchMode = MatchMode.ALL,
    val dateFrom: Instant? = null,
    val dateTo: Instant? = null,
)
