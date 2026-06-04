package dev.jskrzypczak.photovault.core.database.entity

import androidx.room.Entity
import kotlin.time.Instant

/**
 * Local upload ledger row.
 * Composite primary key (fileName + sizeBytes) is the deduplication key.
 */
@Entity(
    tableName = "uploaded_files",
    primaryKeys = ["fileName", "sizeBytes"],
)
data class UploadedFileEntity(
    val fileName: String,
    val sizeBytes: Long,
    val uploadedAt: Instant,
)
