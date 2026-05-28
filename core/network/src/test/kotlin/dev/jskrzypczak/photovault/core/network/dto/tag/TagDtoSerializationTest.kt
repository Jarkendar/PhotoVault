package dev.jarkendar.photovault.core.network.dto.tag

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
    fun `parses tag with all fields`() {
        val parsed = json.decodeFromString<TagDto>(
            """{"id":"tag-001","name":"#morze","photoCount":48}""",
        )
        assertEquals(TagDto(id = "tag-001", name = "#morze", photoCount = 48), parsed)
    }

    @Test
    fun `serializes photoCount as integer`() {
        val tag = TagDto(id = "tag-001", name = "#morze", photoCount = 0)
        val serialized = json.encodeToString(TagDto.serializer(), tag)
        assertEquals(
            Json.parseToJsonElement("""{"id":"tag-001","name":"#morze","photoCount":0}"""),
            Json.parseToJsonElement(serialized),
        )
    }

    @Test
    fun `parses tag list`() {
        val parsed = json.decodeFromString<TagListDto>(
            """{"items":[{"id":"tag-1","name":"#a","photoCount":1},{"id":"tag-2","name":"#b","photoCount":0}]}""",
        )
        assertEquals(2, parsed.items.size)
        assertEquals("tag-1", parsed.items[0].id)
    }
}