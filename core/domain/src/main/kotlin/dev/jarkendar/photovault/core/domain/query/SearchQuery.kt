package dev.jarkendar.photovault.core.domain.query

import dev.jarkendar.photovault.core.domain.id.CategoryId
import dev.jarkendar.photovault.core.domain.id.LabelId
import dev.jarkendar.photovault.core.domain.id.TagId

data class SearchQuery(
    val text: String = "",
    val tagIds: Set<TagId> = emptySet(),
    val categoryIds: Set<CategoryId> = emptySet(),
    val labelIds: Set<LabelId> = emptySet(),
    val favoritesOnly: Boolean = false,
)
