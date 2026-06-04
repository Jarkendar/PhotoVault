package dev.jskrzypczak.photovault.core.domain.model

/**
 * Uniquely identifies an uploaded file from this device.
 * Used as a deduplication key in the local upload ledger.
 */
data class UploadFileKey(val fileName: String, val sizeBytes: Long)
