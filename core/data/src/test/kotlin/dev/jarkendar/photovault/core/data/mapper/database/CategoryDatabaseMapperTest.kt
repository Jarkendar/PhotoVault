package dev.jarkendar.photovault.core.data.mapper.database

import dev.jarkendar.photovault.core.database.entity.CategoryEntity
import dev.jarkendar.photovault.core.domain.id.CategoryId
import dev.jarkendar.photovault.core.domain.model.Category
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
}