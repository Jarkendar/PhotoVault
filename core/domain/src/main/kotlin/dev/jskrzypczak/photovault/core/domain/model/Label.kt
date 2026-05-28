package dev.jskrzypczak.photovault.core.domain.model

import dev.jskrzypczak.photovault.core.domain.id.LabelId

data class Label(
    val id: LabelId,
    val name: String,
    val colorHex: String,
)
