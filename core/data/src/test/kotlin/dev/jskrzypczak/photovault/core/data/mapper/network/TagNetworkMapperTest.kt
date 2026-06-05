package dev.jskrzypczak.photovault.core.data.mapper.network

import dev.jskrzypczak.photovault.core.domain.id.TagId
import dev.jskrzypczak.photovault.core.network.dto.tag.TagDto
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
    fun `toDomain preserves photoCount`() {
        val a = TagDto(id = "tag-001", name = "#morze", photoCount = 0).toDomain()
        val b = TagDto(id = "tag-001", name = "#morze", photoCount = 999).toDomain()
        assertEquals(0, a.photoCount)
        assertEquals(999, b.photoCount)
    }

    @Test
    fun `toDomain maps autoEnabled and rolledOut`() {
        val enabled = TagDto(id = "t1", name = "#bot", photoCount = 1, autoEnabled = true, rolledOut = true).toDomain()
        assertEquals(true, enabled.autoEnabled)
        assertEquals(true, enabled.rolledOut)

        val processing = TagDto(id = "t2", name = "#new", photoCount = 0, autoEnabled = false, rolledOut = false).toDomain()
        assertEquals(false, processing.autoEnabled)
        assertEquals(false, processing.rolledOut)
    }

    @Test
    fun `toDomain uses defaults when autoEnabled and rolledOut absent`() {
        val tag = TagDto(id = "t1", name = "#morze", photoCount = 5).toDomain()
        assertEquals(false, tag.autoEnabled)
        assertEquals(true, tag.rolledOut)
    }
}