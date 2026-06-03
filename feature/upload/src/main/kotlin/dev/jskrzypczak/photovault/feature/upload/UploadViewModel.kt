package dev.jskrzypczak.photovault.feature.upload

import android.content.ContentResolver
import android.net.Uri
import android.provider.MediaStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.workDataOf
import dev.jskrzypczak.photovault.core.common.AppDispatchers
import dev.jskrzypczak.photovault.feature.upload.worker.UploadWorker
import java.util.UUID
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UploadViewModel(
    private val workManager: WorkManager,
    private val contentResolver: ContentResolver,
    private val dispatchers: AppDispatchers,
) : ViewModel() {

    private data class UploadMeta(
        val contentUri: String,
        val fileName: String,
        val sizeBytes: Long,
    )

    private val _autoDetectEnabled = MutableStateFlow(false)
    private val _newPhotosDetected = MutableStateFlow(0)
    private val _uploadMeta = MutableStateFlow<Map<UUID, UploadMeta>>(emptyMap())

    val uiState = combine(
        _autoDetectEnabled,
        _newPhotosDetected,
        workManager.getWorkInfosByTagFlow(UploadWorker.TAG),
        _uploadMeta,
    ) { autoDetect, newPhotos, workInfos, meta ->
        buildUiState(autoDetect, newPhotos, workInfos, meta)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = UploadUiState(),
    )

    fun onPhotosSelected(uris: List<Uri>) {
        val newMeta = mutableMapOf<UUID, UploadMeta>()
        uris.forEach { uri ->
            val fileName = resolveFileName(uri)
            val mimeType = contentResolver.getType(uri) ?: "image/jpeg"
            val sizeBytes = resolveFileSize(uri)
            val request = OneTimeWorkRequestBuilder<UploadWorker>()
                .setInputData(workDataOf(
                    UploadWorker.KEY_CONTENT_URI to uri.toString(),
                    UploadWorker.KEY_FILE_NAME to fileName,
                    UploadWorker.KEY_MIME_TYPE to mimeType,
                ))
                .addTag(UploadWorker.TAG)
                .build()
            newMeta[request.id] = UploadMeta(uri.toString(), fileName, sizeBytes)
            workManager.enqueue(request)
        }
        _uploadMeta.update { it + newMeta }
    }

    fun onToggleAutoDetect(enabled: Boolean) {
        _autoDetectEnabled.value = enabled
        if (enabled) queryNewPhotos()
    }

    fun onCancelUpload(workId: UUID) {
        workManager.cancelWorkById(workId)
        _uploadMeta.update { it - workId }
    }

    private fun queryNewPhotos() {
        viewModelScope.launch(dispatchers.io) {
            val count = runCatching { countNewPhotosFromMediaStore() }.getOrDefault(0)
            _newPhotosDetected.value = count
        }
    }

    private suspend fun countNewPhotosFromMediaStore(): Int = withContext(dispatchers.io) {
        val collection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        val lastUploadTime = System.currentTimeMillis() / 1000 - 7 * 24 * 3600
        val selection = "${MediaStore.Images.Media.DATE_ADDED} > ?"
        contentResolver.query(
            collection,
            arrayOf(MediaStore.Images.Media._ID),
            selection,
            arrayOf(lastUploadTime.toString()),
            null,
        )?.use { it.count } ?: 0
    }

    private fun buildUiState(
        autoDetect: Boolean,
        newPhotos: Int,
        workInfos: List<WorkInfo>,
        meta: Map<UUID, UploadMeta>,
    ): UploadUiState {
        val items = workInfos.mapNotNull { info ->
            val m = meta[info.id] ?: return@mapNotNull null
            info.toUploadItemUiState(m)
        }.toImmutableList()

        val uploadingCount = items.count {
            it.status == UploadItemStatus.UPLOADING || it.status == UploadItemStatus.PROCESSING
        }
        val queuedCount = items.count { it.status == UploadItemStatus.QUEUED }
        val doneCount = items.count { it.status == UploadItemStatus.DONE }
        val activeUpload = items.firstOrNull {
            it.status == UploadItemStatus.UPLOADING || it.status == UploadItemStatus.PROCESSING
        }?.let { ActiveUploadState(it.fileName, it.progress) }

        return UploadUiState(
            autoDetectEnabled = autoDetect,
            newPhotosDetected = newPhotos,
            uploadingCount = uploadingCount,
            queuedCount = queuedCount,
            doneCount = doneCount,
            activeUpload = activeUpload,
            uploads = items,
        )
    }

    private fun WorkInfo.toUploadItemUiState(meta: UploadMeta): UploadItemUiState {
        val progressData = progress
        val progressFloat = progressData.getFloat(UploadWorker.KEY_PROGRESS, 0f)
        val statusStr = progressData.getString(UploadWorker.KEY_STATUS) ?: ""

        val itemStatus = when (state) {
            WorkInfo.State.ENQUEUED, WorkInfo.State.BLOCKED -> UploadItemStatus.QUEUED
            WorkInfo.State.RUNNING -> when (statusStr) {
                "processing" -> UploadItemStatus.PROCESSING
                else -> UploadItemStatus.UPLOADING
            }
            WorkInfo.State.SUCCEEDED -> UploadItemStatus.DONE
            WorkInfo.State.FAILED -> UploadItemStatus.FAILED
            WorkInfo.State.CANCELLED -> UploadItemStatus.CANCELLED
        }

        val errorMsg = if (state == WorkInfo.State.FAILED) {
            outputData.getString(UploadWorker.KEY_ERROR)
        } else null

        return UploadItemUiState(
            workId = id,
            contentUri = meta.contentUri,
            fileName = meta.fileName,
            sizeBytes = meta.sizeBytes,
            status = itemStatus,
            progress = progressFloat,
            mlTags = persistentListOf(),
            errorMessage = errorMsg,
        )
    }

    private fun resolveFileName(uri: Uri): String =
        contentResolver.query(uri, arrayOf(MediaStore.Images.Media.DISPLAY_NAME), null, null, null)
            ?.use { cursor -> if (cursor.moveToFirst()) cursor.getString(0) else null }
            ?: uri.lastPathSegment
            ?: "photo.jpg"

    private fun resolveFileSize(uri: Uri): Long =
        contentResolver.query(uri, arrayOf(MediaStore.Images.Media.SIZE), null, null, null)
            ?.use { cursor -> if (cursor.moveToFirst()) cursor.getLong(0) else 0L }
            ?: 0L
}
