package dev.jskrzypczak.photovault.feature.upload.worker

import android.content.Context
import android.net.Uri
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import dev.jskrzypczak.photovault.core.domain.model.UploadJobStatus
import dev.jskrzypczak.photovault.core.domain.repository.UploadRepository
import kotlinx.coroutines.delay
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class UploadWorker(
    context: Context,
    params: WorkerParameters,
) : CoroutineWorker(context, params), KoinComponent {

    private val uploadRepository: UploadRepository by inject()

    companion object {
        const val KEY_CONTENT_URI = "content_uri"
        const val KEY_FILE_NAME = "file_name"
        const val KEY_MIME_TYPE = "mime_type"
        const val KEY_FILE_SIZE = "file_size"
        const val KEY_PROGRESS = "progress"
        const val KEY_STATUS = "status"
        const val KEY_UPLOAD_ID = "upload_id"
        const val KEY_ERROR = "error"
        const val TAG = "upload"
        private const val POLL_INTERVAL_MS = 1_000L
        private const val MAX_POLLS = 120
    }

    override suspend fun doWork(): Result {
        val uriString = inputData.getString(KEY_CONTENT_URI)
            ?: return Result.failure(workDataOf(KEY_ERROR to "Missing URI"))
        val fileName = inputData.getString(KEY_FILE_NAME)
            ?: return Result.failure(workDataOf(KEY_ERROR to "Missing file name"))
        val mimeType = inputData.getString(KEY_MIME_TYPE) ?: "image/jpeg"

        setProgressAsync(workDataOf(KEY_STATUS to "uploading", KEY_PROGRESS to 0f))

        val bytes = applicationContext.contentResolver
            .openInputStream(Uri.parse(uriString))
            ?.use { it.readBytes() }
            ?: return Result.failure(workDataOf(KEY_ERROR to "Cannot read file"))

        val uploadJob = uploadRepository.uploadPhoto(bytes, fileName, mimeType)
            .getOrElse { e ->
                return Result.failure(workDataOf(KEY_ERROR to (e.message ?: "Upload failed")))
            }

        val uploadId = uploadJob.id
        setProgressAsync(workDataOf(
            KEY_STATUS to uploadJob.status.name.lowercase(),
            KEY_PROGRESS to uploadJob.progress.toFloat(),
            KEY_UPLOAD_ID to uploadId,
        ))

        repeat(MAX_POLLS) {
            delay(POLL_INTERVAL_MS)
            val status = uploadRepository.getUploadStatus(uploadId)
                .getOrElse { return Result.failure(workDataOf(KEY_ERROR to "Polling failed")) }

            setProgressAsync(workDataOf(
                KEY_STATUS to status.status.name.lowercase(),
                KEY_PROGRESS to status.progress.toFloat(),
                KEY_UPLOAD_ID to uploadId,
            ))

            when (status.status) {
                UploadJobStatus.DONE -> {
                    val sizeBytes = inputData.getLong(KEY_FILE_SIZE, -1L)
                    if (sizeBytes >= 0) uploadRepository.rememberUploaded(fileName, sizeBytes)
                    return Result.success(workDataOf(KEY_UPLOAD_ID to uploadId))
                }
                UploadJobStatus.FAILED, UploadJobStatus.CANCELLED ->
                    return Result.failure(workDataOf(KEY_ERROR to (status.error ?: "Upload failed")))
                else -> Unit
            }
        }

        return Result.failure(workDataOf(KEY_ERROR to "Upload timed out"))
    }
}
