package dev.jarkendar.photovault.core.data.mapper.network

import dev.jarkendar.photovault.core.data.fixtures.PhotoTestFixtures
import dev.jarkendar.photovault.core.domain.id.CategoryId
import dev.jarkendar.photovault.core.domain.id.LabelId
import dev.jarkendar.photovault.core.domain.id.TagId
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class PhotoNetworkMapperTest {

    @Test
    fun `toDomain maps all fields correctly`() {
        val result = PhotoTestFixtures.PHOTO_DTO.toDomain()
        assertEquals(PhotoTestFixtures.PHOTO_DOMAIN, result)
    }

    @Test
    fun `toDomain with null capturedAt`() {
        val dto = PhotoTestFixtures.PHOTO_DTO.copy(capturedAt = null)
        assertNull(dto.toDomain().capturedAt)
    }

    @Test
    fun `toDomain with null camera`() {
        val dto = PhotoTestFixtures.PHOTO_DTO.copy(camera = null)
        assertNull(dto.toDomain().camera)
    }

    @Test
    fun `toDomain with null location`() {
        val dto = PhotoTestFixtures.PHOTO_DTO.copy(location = null)
        assertNull(dto.toDomain().location)
    }

    @Test
    fun `toDomain with empty tags categories labels`() {
        val dto = PhotoTestFixtures.PHOTO_DTO.copy(
            tags = emptyList(),
            categories = emptyList(),
            labels = emptyList(),
        )
        val domain = dto.toDomain()
        assertEquals(emptyList(), domain.tags)
        assertEquals(emptyList(), domain.categories)
        assertEquals(emptyList(), domain.labels)
    }

    @Test
    fun `toDomain preserves tag ordering`() {
        val tagA = PhotoTestFixtures.TAG_DTO.copy(id = "a", name = "#a")
        val tagB = PhotoTestFixtures.TAG_DTO.copy(id = "b", name = "#b")
        val tagC = PhotoTestFixtures.TAG_DTO.copy(id = "c", name = "#c")
        val dto = PhotoTestFixtures.PHOTO_DTO.copy(tags = listOf(tagB, tagA, tagC))
        assertEquals(
            listOf(TagId("b"), TagId("a"), TagId("c")),
            dto.toDomain().tags.map { it.id },
        )
    }

    @Test
    fun `PhotoPageDto toDomain returns mapped list`() {
        val result = PhotoTestFixtures.PHOTO_PAGE_DTO.toDomain()
        assertEquals(2, result.size)
        assertEquals(PhotoTestFixtures.PHOTO_DOMAIN, result[0])
    }

    @Test
    fun `Category and Label mappers preserve ids`() {
        val domain = PhotoTestFixtures.PHOTO_DTO.toDomain()
        assertEquals(CategoryId("cat-001"), domain.categories.single().id)
        assertEquals(LabelId("label-orange"), domain.labels.single().id)
    }
}