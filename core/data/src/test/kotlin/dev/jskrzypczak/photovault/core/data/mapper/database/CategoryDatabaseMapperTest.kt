package dev.jskrzypczak.photovault.core.data.mapper.database

import dev.jskrzypczak.photovault.core.database.entity.CategoryEntity
import dev.jskrzypczak.photovault.core.domain.id.CategoryId
import dev.jskrzypczak.photovault.core.domain.model.Category
import kotlin.test.Test
import kotlin.test.assertEquals

class CategoryDatabaseMapperTest {

    @Test
    fun `CategoryEntity toDomain maps fields`() {
        val cat = CategoryEntity(id = "cat-001", name = "Natura", colorHex = "#FF8B45").toDomain()
        assertEquals(Category(id = CategoryId("cat-001"), name = "Natura", colorHex = "#FF8B45"), cat)
    }

    @Test
    fun `Category toEntity round-trips`() {
        val original = Category(id = CategoryId("cat-001"), name = "Natura", colorHex = "#FF8B45")
        assertEquals(original, original.toEntity().toDomain())
    }

    @Test
    fun `CategoryEntity toDomain maps autoEnabled and rolledOut`() {
        val entity = CategoryEntity(id = "cat-001", name = "Natura", colorHex = "#FF8B45", autoEnabled = true, rolledOut = false)
        val cat = entity.toDomain()
        assertEquals(true, cat.autoEnabled)
        assertEquals(false, cat.rolledOut)
    }

    @Test
    fun `Category toEntity round-trips autoEnabled and rolledOut`() {
        val original = Category(id = CategoryId("cat-001"), name = "Natura", colorHex = "#FF8B45", autoEnabled = true, rolledOut = false)
        assertEquals(original, original.toEntity().toDomain())
    }
}