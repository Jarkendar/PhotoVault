package dev.jarkendar.photovault.core.network.dto.label

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LabelDto(
    val id: String,
    val name: LabelName,
    val colorHex: String,
    val photoCount: Int,
)

@Serializable
data class LabelListDto(
    val items: List<LabelDto>,
)

@Serializable
enum class LabelName {
    @SerialName("red") RED,
    @SerialName("orange") ORANGE,
    @SerialName("yellow") YELLOW,
    @SerialName("green") GREEN,
    @SerialName("blue") BLUE,
    @SerialName("purple") PURPLE,
}