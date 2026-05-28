package dev.jarkendar.photovault.core.network.error

import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class ProblemDetailsDtoSerializationTest {

    private val json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
        isLenient = true
    }

    @Test
    fun `parses standard RFC 7807 without errors extension`() {
        val parsed = json.decodeFromString<ProblemDetailsDto>(
            """
            {
              "type":"https://photovault.local/errors/photo-not-found",
              "title":"Photo Not Found",
              "status":404,
              "detail":"No photo with id photo-abc"
            }
            """.trimIndent(),
        )
        assertEquals("https://photovault.local/errors/photo-not-found", parsed.type)
        assertEquals(404, parsed.status)
        assertEquals("No photo with id photo-abc", parsed.detail)
        assertNull(parsed.errors)
    }

    @Test
    fun `parses validation problem with errors extension`() {
        val parsed = json.decodeFromString<ProblemDetailsDto>(
            """
            {
              "type":"https://photovault.local/errors/validation-failed",
              "title":"Validation Failed",
              "status":400,
              "errors":{"name":["must not be blank"],"colorHex":["must match #RRGGBB"]}
            }
            """.trimIndent(),
        )
        assertNotNull(parsed.errors)
        assertEquals(2, parsed.errors!!.size)
        assertEquals(listOf("must not be blank"), parsed.errors!!["name"])
    }

    @Test
    fun `serializes back to JSON equivalent to input`() {
        val source = """
            {"type":"about:blank","title":"X","status":500,"detail":"oops"}
        """.trimIndent()
        val parsed = json.decodeFromString<ProblemDetailsDto>(source)
        val serialized = json.encodeToString(ProblemDetailsDto.serializer(), parsed)
        assertEquals(Json.parseToJsonElement(source), Json.parseToJsonElement(serialized))
    }
}