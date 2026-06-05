package dev.jskrzypczak.photovault.core.data.mapper.database

import dev.jskrzypczak.photovault.core.database.entity.TagEntity
import dev.jskrzypczak.photovault.core.domain.id.TagId
import dev.jskrzypczak.photovault.core.domain.model.Tag
import kotlin.test.Test
import kotlin.test.assertEquals

class TagDatabaseMapperTest {

    @Test
    fun `TagEntity toDomain maps id and name`() {
        val tag = TagEntity(id = "tag-001", name = "#morze").toDomain()
        assertEquals(Tag(id = TagId("tag-001"), name = "#morze"), tag)
    }

    @Test
    fun `Tag toEntity round-trips`() {
        val original = Tag(id = TagId("tag-001"), name = "#morze")
        assertEquals(original, original.toEntity().toDomain())
    }

    @Test
    fun `TagEntity toDomain maps autoEnabled and rolledOut`() {
        val entity = TagEntity(id = "t1", name = "#bot", autoEnabled = true, rolledOut = false)
        val tag = entity.toDomain()
        assertEquals(true, tag.autoEnabled)
        assertEquals(false, tag.rolledOut)
    }

    @Test
    fun `Tag toEntity round-trips autoEnabled and rolledOut`() {
        val original = Tag(id = TagId("t1"), name = "#bot", autoEnabled = true, rolledOut = false)
        assertEquals(original, original.toEntity().toDomain())
    }
}