package dev.jskrzypczak.photovault.core.domain.query

/** Combination logic for multi-value filters (tagIds, categoryIds, labelIds). */
enum class MatchMode {
    /** Photo must match ALL listed values (AND). */
    ALL,

    /** Photo must match AT LEAST ONE listed value (OR). */
    ANY,
}
