package dev.jarkendar.photovault.core.data.mapper.database

import dev.jarkendar.photovault.core.database.entity.TagEntity
import dev.jarkendar.photovault.core.domain.id.TagId
import dev.jarkendar.photovault.core.domain.model.Tag
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
}