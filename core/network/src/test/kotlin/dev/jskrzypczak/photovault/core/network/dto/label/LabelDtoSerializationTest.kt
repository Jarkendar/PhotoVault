package dev.jarkendar.photovault.core.network.dto.label

import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

class LabelDtoSerializationTest {

    private val json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
        isLenient = true
    }

    @Test
    fun `parses all six label values`() {
        val cases = listOf(
            "red" to LabelName.RED,
            "orange" to LabelName.ORANGE,
            "yellow" to LabelName.YELLOW,
            "green" to LabelName.GREEN,
            "blue" to LabelName.BLUE,
            "purple" to LabelName.PURPLE,
        )
        for ((serialized, expected) in cases) {
            val parsed = json.decodeFromString<LabelDto>(
                """{"id":"label-$serialized","name":"$serialized","colorHex":"#000000","photoCount":1}""",
            )
            assertEquals(expected, parsed.name, "Mapping for $serialized")
        }
    }

    @Test
    fun `serializes LabelName as snake-case`() {
        val label = LabelDto("label-orange", LabelName.ORANGE, "#FF8B45", 23)
        val serialized = json.encodeToString(LabelDto.serializer(), label)
        assertEquals(
            Json.parseToJsonElement(
                """{"id":"label-orange","name":"orange","colorHex":"#FF8B45","photoCount":23}""",
            ),
            Json.parseToJsonElement(serialized),
        )
    }

    @Test
    fun `parses label list with all six labels`() {
        val parsed = json.decodeFromString<LabelListDto>(
            """
            {"items":[
              {"id":"l1","name":"red","colorHex":"#FF0000","photoCount":1},
              {"id":"l2","name":"orange","colorHex":"#FF8B00","photoCount":2},
              {"id":"l3","name":"yellow","colorHex":"#FFFF00","photoCount":3},
              {"id":"l4","name":"green","colorHex":"#00FF00","photoCount":4},
              {"id":"l5","name":"blue","colorHex":"#0000FF","photoCount":5},
              {"id":"l6","name":"purple","colorHex":"#8000FF","photoCount":6}
            ]}
            """.trimIndent(),
        )
        assertEquals(6, parsed.items.size)
    }
}