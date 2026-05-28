package dev.jarkendar.photovault.core.network.dto.photo

import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class LocationDtoSerializationTest {

    private val json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
        isLenient = true
    }

    @Test
    fun `parses location with placeName`() {
        val parsed = json.decodeFromString<LocationDto>(
            """{"latitude":54.4641,"longitude":18.5734,"placeName":"Sopot, PL"}""",
        )
        assertEquals(54.4641, parsed.latitude)
        assertEquals(18.5734, parsed.longitude)
        assertEquals("Sopot, PL", parsed.placeName)
    }

    @Test
    fun `parses location with placeName null`() {
        val parsed = json.decodeFromString<LocationDto>(
            """{"latitude":54.4641,"longitude":18.5734,"placeName":null}""",
        )
        assertNull(parsed.placeName)
    }

    @Test
    fun `parses location with placeName missing`() {
        val parsed = json.decodeFromString<LocationDto>(
            """{"latitude":54.4641,"longitude":18.5734}""",
        )
        assertNull(parsed.placeName)
    }
}