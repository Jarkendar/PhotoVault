package dev.jarkendar.photovault.core.data.mapper.network

import dev.jarkendar.photovault.core.domain.id.LabelId
import dev.jarkendar.photovault.core.network.dto.label.LabelDto
import dev.jarkendar.photovault.core.network.dto.label.LabelName
import kotlin.test.Test
import kotlin.test.assertEquals

class LabelNetworkMapperTest {

    @Test
    fun `toDomain maps each LabelName to lowercase string`() {
        val cases = mapOf(
            LabelName.RED to "red",
            LabelName.ORANGE to "orange",
            LabelName.YELLOW to "yellow",
            LabelName.GREEN to "green",
            LabelName.BLUE to "blue",
            LabelName.PURPLE to "purple",
        )
        for ((enum, expected) in cases) {
            val result = LabelDto(id = "label-$expected", name = enum, colorHex = "#000000", photoCount = 0).toDomain()
            assertEquals(expected, result.name, "Mapping for $enum")
        }
    }

    @Test
    fun `toDomain preserves id and colorHex`() {
        val result = LabelDto(
            id = "label-orange",
            name = LabelName.ORANGE,
            colorHex = "#FF8B45",
            photoCount = 23,
        ).toDomain()
        assertEquals(LabelId("label-orange"), result.id)
        assertEquals("#FF8B45", result.colorHex)
    }
}