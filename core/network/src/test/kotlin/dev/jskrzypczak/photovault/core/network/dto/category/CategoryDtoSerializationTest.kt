package dev.jarkendar.photovault.core.network.dto.category

import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

class CategoryDtoSerializationTest {

    private val json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
        isLenient = true
    }

    @Test
    fun `parses category with hex color`() {
        val parsed = json.decodeFromString<CategoryDto>(
            """{"id":"cat-001","name":"Natura","colorHex":"#FF8B45","photoCount":48}""",
        )
        assertEquals(CategoryDto("cat-001", "Natura", "#FF8B45", 48), parsed)
    }

    @Test
    fun `serializes colorHex preserves casing`() {
        val cat = CategoryDto("cat-001", "Natura", "#FF8B45", 0)
        val serialized = json.encodeToString(CategoryDto.serializer(), cat)
        assertEquals(
            Json.parseToJsonElement(
                """{"id":"cat-001","name":"Natura","colorHex":"#FF8B45","photoCount":0}""",
            ),
            Json.parseToJsonElement(serialized),
        )
    }

    @Test
    fun `parses category list`() {
        val parsed = json.decodeFromString<CategoryListDto>(
            """{"items":[{"id":"cat-1","name":"A","colorHex":"#000000","photoCount":1}]}""",
        )
        assertEquals(1, parsed.items.size)
    }
}