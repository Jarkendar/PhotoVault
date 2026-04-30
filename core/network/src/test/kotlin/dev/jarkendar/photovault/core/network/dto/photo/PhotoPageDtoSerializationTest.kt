package dev.jarkendar.photovault.core.network.dto.photo

import dev.jarkendar.photovault.core.network.fixtures.PhotoDtoFixtures
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class PhotoPageDtoSerializationTest {

    private val json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
        isLenient = true
    }

    @Test
    fun `parses page with items and nextCursor`() {
        val parsed = json.decodeFromString<PhotoPageDto>(PhotoDtoFixtures.SAMPLE_PHOTO_PAGE_JSON)
        assertEquals(1, parsed.items.size)
        assertEquals("cursor-abc", parsed.nextCursor)
        assertTrue(parsed.hasMore)
    }

    @Test
    fun `parses page with hasMore false and nextCursor null`() {
        val parsed = json.decodeFromString<PhotoPageDto>(PhotoDtoFixtures.EMPTY_PHOTO_PAGE_JSON)
        assertNull(parsed.nextCursor)
        assertFalse(parsed.hasMore)
    }

    @Test
    fun `parses empty items list`() {
        val parsed = json.decodeFromString<PhotoPageDto>(PhotoDtoFixtures.EMPTY_PHOTO_PAGE_JSON)
        assertEquals(emptyList(), parsed.items)
    }
}