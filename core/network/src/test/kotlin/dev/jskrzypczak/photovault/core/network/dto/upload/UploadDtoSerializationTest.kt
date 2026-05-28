package dev.jarkendar.photovault.core.network.dto.upload

import dev.jarkendar.photovault.core.network.fixtures.UploadDtoFixtures
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class UploadDtoSerializationTest {

    private val json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
        isLenient = true
    }

    @Test
    fun `parses each UploadStatus value`() {
        val cases = listOf(
            "created" to UploadStatus.CREATED,
            "uploading" to UploadStatus.UPLOADING,
            "processing" to UploadStatus.PROCESSING,
            "done" to UploadStatus.DONE,
            "failed" to UploadStatus.FAILED,
            "cancelled" to UploadStatus.CANCELLED,
        )
        for ((serialized, expected) in cases) {
            val u = json.decodeFromString<UploadDto>(
                """
                {
                  "id":"u-$serialized","fileName":"f.jpg","sizeBytes":1,"uploadedBytes":1,
                  "status":"$serialized","progress":0.5,"createdAt":"2026-01-01T00:00:00Z"
                }
                """.trimIndent(),
            )
            assertEquals(expected, u.status, "Mapping for $serialized")
        }
    }

    @Test
    fun `parses upload with photoId null in processing status`() {
        val parsed = json.decodeFromString<UploadDto>(UploadDtoFixtures.UPLOAD_PROCESSING_JSON)
        assertEquals(UploadStatus.PROCESSING, parsed.status)
        assertNull(parsed.photoId)
        assertNull(parsed.error)
    }

    @Test
    fun `parses upload with photoId set when done`() {
        val parsed = json.decodeFromString<UploadDto>(UploadDtoFixtures.UPLOAD_DONE_JSON)
        assertEquals(UploadStatus.DONE, parsed.status)
        assertEquals("photo-abc123", parsed.photoId)
    }

    @Test
    fun `parses upload with error set when failed`() {
        val parsed = json.decodeFromString<UploadDto>(UploadDtoFixtures.UPLOAD_FAILED_JSON)
        assertEquals(UploadStatus.FAILED, parsed.status)
        assertNotNull(parsed.error)
    }

    @Test
    fun `parses upload list`() {
        val parsed = json.decodeFromString<UploadListDto>(
            """{"items":[${UploadDtoFixtures.UPLOAD_DONE_JSON}]}""",
        )
        assertEquals(1, parsed.items.size)
        assertEquals(UploadStatus.DONE, parsed.items[0].status)
    }
}