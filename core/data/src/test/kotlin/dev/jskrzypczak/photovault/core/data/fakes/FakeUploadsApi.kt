package dev.jskrzypczak.photovault.core.data.fakes

import dev.jskrzypczak.photovault.core.network.api.UploadsApi
import dev.jskrzypczak.photovault.core.network.dto.upload.UploadDto
import dev.jskrzypczak.photovault.core.network.dto.upload.UploadListDto
import dev.jskrzypczak.photovault.core.network.dto.upload.UploadStatus

class FakeUploadsApi : UploadsApi {

    var nextUploadResponse: Result<UploadDto> = Result.failure(NotImplementedError())
    var nextGetResponse: Result<UploadDto> = Result.failure(NotImplementedError())
    var nextDeleteResponse: Result<Unit> = Result.success(Unit)
    var nextListResponse: Result<UploadListDto> = Result.success(UploadListDto(emptyList()))

    data class UploadCall(val fileName: String, val mimeType: String, val byteCount: Int)

    val uploadCalls = mutableListOf<UploadCall>()
    val getCalls = mutableListOf<String>()
    val deleteCalls = mutableListOf<String>()
    var capturedListStatuses: List<UploadStatus>? = null

    override suspend fun uploadPhoto(
        bytes: ByteArray,
        fileName: String,
        mimeType: String,
    ): Result<UploadDto> {
        uploadCalls += UploadCall(fileName, mimeType, bytes.size)
        return nextUploadResponse
    }

    override suspend fun listUploads(statuses: List<UploadStatus>?): Result<UploadListDto> {
        capturedListStatuses = statuses
        return nextListResponse
    }

    override suspend fun getUpload(id: String): Result<UploadDto> {
        getCalls += id
        return nextGetResponse
    }

    override suspend fun deleteUpload(id: String): Result<Unit> {
        deleteCalls += id
        return nextDeleteResponse
    }
}
