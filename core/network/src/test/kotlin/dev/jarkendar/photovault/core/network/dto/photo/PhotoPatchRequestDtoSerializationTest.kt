package dev.jarkendar.photovault.core.network.dto.photo

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PhotoPatchRequestDtoSerializationTest {

    private val json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
        isLenient = true
    }

    @Test
    fun `serializes with only isFavorite`() {
        val request = PhotoPatchRequestDto(isFavorite = true)
        val serialized = json.encodeToString(PhotoPatchRequestDto.serializer(), request)
        val obj = Json.parseToJsonElement(serialized) as JsonObject
        assertEquals(1, obj.size)
        assertTrue(obj.containsKey("isFavorite"))
    }

    @Test
    fun `serializes with only tagIds`() {
        val request = PhotoPatchRequestDto(tagIds = listOf("tag-1", "tag-2"))
        val serialized = json.encodeToString(PhotoPatchRequestDto.serializer(), request)
        val obj = Json.parseToJsonElement(serialized) as JsonObject
        assertEquals(1, obj.size)
        assertTrue(obj.containsKey("tagIds"))
    }

    @Test
    fun `serializes with all fields populated`() {
        val request = PhotoPatchRequestDto(
            isFavorite = false,
            tagIds = listOf("tag-1"),
            categoryIds = listOf("cat-1"),
            labelIds = listOf("label-red"),
        )
        val serialized = json.encodeToString(PhotoPatchRequestDto.serializer(), request)
        val obj = Json.parseToJsonElement(serialized) as JsonObject
        assertEquals(setOf("isFavorite", "tagIds", "categoryIds", "labelIds"), obj.keys)
    }

    @Test
    fun `empty request omits all nullable fields`() {
        val request = PhotoPatchRequestDto()
        val serialized = json.encodeToString(PhotoPatchRequestDto.serializer(), request)
        val obj = Json.parseToJsonElement(serialized) as JsonObject
        assertTrue(obj.isEmpty())
    }
}
