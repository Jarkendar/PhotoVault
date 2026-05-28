package dev.jarkendar.photovault.core.data.mapper.network

import dev.jarkendar.photovault.core.domain.id.TagId
import dev.jarkendar.photovault.core.network.dto.tag.TagDto
import kotlin.test.Test
import kotlin.test.assertEquals

class TagNetworkMapperTest {

    @Test
    fun `toDomain extracts id and name`() {
        val tag = TagDto(id = "tag-001", name = "#morze", photoCount = 7).toDomain()
        assertEquals(TagId("tag-001"), tag.id)
        assertEquals("#morze", tag.name)
    }

    @Test
    fun `toDomain ignores photoCount`() {
        val a = TagDto(id = "tag-001", name = "#morze", photoCount = 0).toDomain()
        val b = TagDto(id = "tag-001", name = "#morze", photoCount = 999).toDomain()
        assertEquals(a, b)
    }
}