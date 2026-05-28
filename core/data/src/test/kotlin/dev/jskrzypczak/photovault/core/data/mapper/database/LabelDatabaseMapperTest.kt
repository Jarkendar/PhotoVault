package dev.jskrzypczak.photovault.core.data.mapper.database

import dev.jskrzypczak.photovault.core.database.entity.LabelEntity
import dev.jskrzypczak.photovault.core.domain.id.LabelId
import dev.jskrzypczak.photovault.core.domain.model.Label
import kotlin.test.Test
import kotlin.test.assertEquals

class LabelDatabaseMapperTest {

    @Test
    fun `LabelEntity toDomain maps fields`() {
        val label = LabelEntity(id = "label-orange", name = "orange", colorHex = "#FF8B45").toDomain()
        assertEquals(Label(id = LabelId("label-orange"), name = "orange", colorHex = "#FF8B45"), label)
    }

    @Test
    fun `Label toEntity round-trips`() {
        val original = Label(id = LabelId("label-orange"), name = "orange", colorHex = "#FF8B45")
        assertEquals(original, original.toEntity().toDomain())
    }
}