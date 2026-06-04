package dev.jskrzypczak.photovault.core.network.dto.photo

import dev.jskrzypczak.photovault.core.network.fixtures.PhotoDtoFixtures
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class PhotoDtoSerializationTest {

    private val json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
        isLenient = true
        coerceInputValues = true
        encodeDefaults = true
    }

    @Test
    fun `parses photo with all fields`() {
        val parsed = json.decodeFromString<PhotoDto>(PhotoDtoFixtures.SAMPLE_PHOTO_JSON)
        assertEquals(PhotoDtoFixtures.SAMPLE_PHOTO_DTO, parsed)
    }

    @Test
    fun `parses photo with null nullable fields`() {
        val parsed = json.decodeFromString<PhotoDto>(PhotoDtoFixtures.SAMPLE_PHOTO_NO_OPTIONALS_JSON)
        assertEquals(PhotoDtoFixtures.SAMPLE_PHOTO_NO_OPTIONALS_DTO, parsed)
        assertNull(parsed.capturedAt)
        assertNull(parsed.camera)
        assertNull(parsed.location)
    }

    @Test
    fun `parses photo with empty tags categories labels`() {
        val parsed = json.decodeFromString<PhotoDto>(PhotoDtoFixtures.SAMPLE_PHOTO_NO_OPTIONALS_JSON)
        assertEquals(emptyList(), parsed.tags)
        assertEquals(emptyList(), parsed.categories)
        assertEquals(emptyList(), parsed.labels)
    }

    @Test
    fun `serializes back to JSON equivalent to input`() {
        val parsed = json.decodeFromString<PhotoDto>(PhotoDtoFixtures.SAMPLE_PHOTO_JSON)
        val serialized = json.encodeToString(PhotoDto.serializer(), parsed)
        assertEquals(
            Json.parseToJsonElement(PhotoDtoFixtures.SAMPLE_PHOTO_JSON),
            Json.parseToJsonElement(serialized),
        )
    }

    @Test
    fun `handles ProcessingStatus enum values`() {
        val ready = json.decodeFromString<PhotoDto>(PhotoDtoFixtures.SAMPLE_PHOTO_JSON)
        assertEquals(ProcessingStatus.READY, ready.processingStatus)

        val processing = json.decodeFromString<PhotoDto>(PhotoDtoFixtures.SAMPLE_PHOTO_NO_OPTIONALS_JSON)
        assertEquals(ProcessingStatus.PROCESSING, processing.processingStatus)
    }

    @Test
    fun `parses pending_categorization processingStatus`() {
        val pendingJson = PhotoDtoFixtures.SAMPLE_PHOTO_NO_OPTIONALS_JSON
            .replace("\"processing\"", "\"pending_categorization\"")
        val parsed = json.decodeFromString<PhotoDto>(pendingJson)
        assertEquals(ProcessingStatus.PENDING_CATEGORIZATION, parsed.processingStatus)
    }

    @Test
    fun `coerces unknown processingStatus to READY instead of crashing`() {
        // Future server statuses (e.g. "archived") must not crash the gallery.
        // coerceInputValues=true maps unknown values → field default (ProcessingStatus.READY).
        val unknownStatusJson = PhotoDtoFixtures.SAMPLE_PHOTO_NO_OPTIONALS_JSON
            .replace("\"processing\"", "\"archived\"")
        val parsed = json.decodeFromString<PhotoDto>(unknownStatusJson)
        assertEquals(ProcessingStatus.READY, parsed.processingStatus)
    }
}
