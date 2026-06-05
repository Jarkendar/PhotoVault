package dev.jskrzypczak.photovault.core.data.mapper.network

import dev.jskrzypczak.photovault.core.domain.id.CategoryId
import dev.jskrzypczak.photovault.core.network.dto.category.CategoryDto
import kotlin.test.Test
import kotlin.test.assertEquals

class CategoryNetworkMapperTest {

    @Test
    fun `toDomain maps id name colorHex`() {
        val result = CategoryDto(
            id = "cat-001",
            name = "Natura",
            colorHex = "#FF8B45",
            photoCount = 48,
        ).toDomain()
        assertEquals(CategoryId("cat-001"), result.id)
        assertEquals("Natura", result.name)
        assertEquals("#FF8B45", result.colorHex)
    }

    @Test
    fun `toDomain maps autoEnabled and rolledOut`() {
        val result = CategoryDto(
            id = "cat-001",
            name = "Natura",
            colorHex = "#FF8B45",
            photoCount = 10,
            autoEnabled = true,
            rolledOut = false,
        ).toDomain()
        assertEquals(true, result.autoEnabled)
        assertEquals(false, result.rolledOut)
    }

    @Test
    fun `toDomain uses defaults when autoEnabled and rolledOut absent`() {
        val result = CategoryDto(id = "cat-001", name = "Natura", colorHex = "#FF8B45", photoCount = 0).toDomain()
        assertEquals(false, result.autoEnabled)
        assertEquals(true, result.rolledOut)
    }
}