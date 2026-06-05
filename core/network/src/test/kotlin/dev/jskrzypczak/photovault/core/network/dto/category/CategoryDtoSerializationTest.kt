package dev.jskrzypczak.photovault.core.network.dto.category

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
    fun `parses category with all fields including autoEnabled and rolledOut`() {
        val parsed = json.decodeFromString<CategoryDto>(
            """{"id":"cat-001","name":"Natura","colorHex":"#FF8B45","photoCount":48,"autoEnabled":true,"rolledOut":false}""",
        )
        assertEquals(CategoryDto("cat-001", "Natura", "#FF8B45", 48, autoEnabled = true, rolledOut = false), parsed)
    }

    @Test
    fun `parses category without autoEnabled and rolledOut — falls back to defaults`() {
        val parsed = json.decodeFromString<CategoryDto>(
            """{"id":"cat-001","name":"Natura","colorHex":"#FF8B45","photoCount":48}""",
        )
        assertEquals(false, parsed.autoEnabled)
        assertEquals(true, parsed.rolledOut)
    }

    @Test
    fun `serializes colorHex preserves casing and includes non-default flags`() {
        // autoEnabled = true is non-default (default false), rolledOut = false is non-default (default true)
        val cat = CategoryDto("cat-001", "Natura", "#FF8B45", 0, autoEnabled = true, rolledOut = false)
        val serialized = json.encodeToString(CategoryDto.serializer(), cat)
        assertEquals(
            Json.parseToJsonElement(
                """{"id":"cat-001","name":"Natura","colorHex":"#FF8B45","photoCount":0,"autoEnabled":true,"rolledOut":false}""",
            ),
            Json.parseToJsonElement(serialized),
        )
    }

    @Test
    fun `parses category list`() {
        val parsed = json.decodeFromString<CategoryListDto>(
            """{"items":[{"id":"cat-1","name":"A","colorHex":"#000000","photoCount":1,"autoEnabled":false,"rolledOut":true}]}""",
        )
        assertEquals(1, parsed.items.size)
        assertEquals(false, parsed.items[0].autoEnabled)
    }

    @Test
    fun `CategoryUpdateRequestDto serializes only non-null fields`() {
        val request = CategoryUpdateRequestDto(autoEnabled = true)
        val serialized = json.encodeToString(CategoryUpdateRequestDto.serializer(), request)
        assertEquals(
            Json.parseToJsonElement("""{"autoEnabled":true}"""),
            Json.parseToJsonElement(serialized),
        )
    }
}