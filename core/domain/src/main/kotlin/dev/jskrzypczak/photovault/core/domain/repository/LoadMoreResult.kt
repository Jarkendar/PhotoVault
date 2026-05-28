package dev.jarkendar.photovault.core.domain.repository

data class LoadMoreResult(
    val nextCursor: String?,
    val hasMore: Boolean,
)