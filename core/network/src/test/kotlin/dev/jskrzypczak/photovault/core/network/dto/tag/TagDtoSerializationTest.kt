package dev.jskrzypczak.photovault.core.network.dto.tag

import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

class TagDtoSerializationTest {

    private val json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
        isLenient = true
    }

    @Test
    fun `parses tag with all fields including autoEnabled and rolledOut`() {
        val parsed = json.decodeFromString<TagDto>(
            """{"id":"tag-001","name":"#morze","photoCount":48,"autoEnabled":true,"rolledOut":false}""",
        )
        assertEquals(
            TagDto(id = "tag-001", name = "#morze", photoCount = 48, autoEnabled = true, rolledOut = false),
            parsed,
        )
    }

    @Test
    fun `parses tag without autoEnabled and rolledOut — falls back to defaults`() {
        val parsed = json.decodeFromString<TagDto>(
            """{"id":"tag-001","name":"#morze","photoCount":48}""",
        )
        assertEquals(false, parsed.autoEnabled)
        assertEquals(true, parsed.rolledOut)
    }

    @Test
    fun `serializes tag with non-default autoEnabled and rolledOut`() {
        // rolledOut = false is non-default (default is true), autoEnabled = true is non-default (default is false)
        val tag = TagDto(id = "tag-001", name = "#morze", photoCount = 0, autoEnabled = true, rolledOut = false)
        val serialized = json.encodeToString(TagDto.serializer(), tag)
        val element = Json.parseToJsonElement(serialized)
        assertEquals(
            Json.parseToJsonElement(
                """{"id":"tag-001","name":"#morze","photoCount":0,"autoEnabled":true,"rolledOut":false}""",
            ),
            element,
        )
    }

    @Test
    fun `parses tag list`() {
        val parsed = json.decodeFromString<TagListDto>(
            """{"items":[{"id":"tag-1","name":"#a","photoCount":1,"autoEnabled":false,"rolledOut":true},{"id":"tag-2","name":"#b","photoCount":0,"autoEnabled":true,"rolledOut":false}]}""",
        )
        assertEquals(2, parsed.items.size)
        assertEquals("tag-1", parsed.items[0].id)
        assertEquals(true, parsed.items[1].autoEnabled)
    }

    @Test
    fun `TagUpdateRequestDto serializes only non-null fields`() {
        val request = TagUpdateRequestDto(autoEnabled = true)
        val serialized = json.encodeToString(TagUpdateRequestDto.serializer(), request)
        val element = Json.parseToJsonElement(serialized)
        assertEquals(
            Json.parseToJsonElement("""{"autoEnabled":true}"""),
            element,
        )
    }

    @Test
    fun `TagUpdateRequestDto can carry all optional fields`() {
        val request = TagUpdateRequestDto(name = "#newname", autoEnabled = false, rolledOut = true)
        val serialized = json.encodeToString(TagUpdateRequestDto.serializer(), request)
        val element = Json.parseToJsonElement(serialized)
        assertEquals(
            Json.parseToJsonElement("""{"name":"#newname","autoEnabled":false,"rolledOut":true}"""),
            element,
        )
    }
}