package dev.jskrzypczak.photovault.core.domain.model

enum class ProcessingStatus {
    /** Assets (thumbnail, medium) are still being generated — 423 on asset endpoints. */
    PROCESSING,

    /** Assets are fully accessible; photo is waiting for the categoriser to run. */
    PENDING_CATEGORIZATION,

    /** Categorisation complete — terminal state. */
    READY,
}
