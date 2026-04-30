package dev.jarkendar.photovault.core.data.mapper.network

import dev.jarkendar.photovault.core.domain.id.CategoryId
import dev.jarkendar.photovault.core.network.dto.category.CategoryDto
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
}