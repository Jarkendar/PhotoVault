package dev.jskrzypczak.photovault.feature.upload

import java.util.UUID
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

enum class UploadItemStatus { QUEUED, UPLOADING, PROCESSING, DONE, FAILED, CANCELLED }

data class UploadItemUiState(
    val workId: UUID,
    val contentUri: String,
    val fileName: String,
    val sizeBytes: Long,
    val status: UploadItemStatus,
    val progress: Float,
    val mlTags: ImmutableList<String>,
    /** Non-null only when [status] is [UploadItemStatus.FAILED]; contains the raw error message. */
    val errorMessage: String? = null,
    /** Camera make+model from EXIF, e.g. "Google Pixel 8 Pro". */
    val camera: String? = null,
    /** Capture datetime from EXIF, formatted as "YYYY-MM-DD HH:MM". */
    val capturedAt: String? = null,
)

data class ActiveUploadState(
    val fileName: String,
    val progress: Float,
)

data class UploadUiState(
    val autoDetectEnabled: Boolean = false,
    val newPhotosDetected: Int = 0,
    val uploadingCount: Int = 0,
    val queuedCount: Int = 0,
    val doneCount: Int = 0,
    val activeUpload: ActiveUploadState? = null,
    val uploads: ImmutableList<UploadItemUiState> = persistentListOf(),
)
