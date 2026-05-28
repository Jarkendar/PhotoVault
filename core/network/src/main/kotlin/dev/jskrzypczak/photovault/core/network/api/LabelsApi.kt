package dev.jskrzypczak.photovault.core.network.api

import dev.jskrzypczak.photovault.core.network.dto.label.LabelListDto

interface LabelsApi {
    suspend fun listLabels(): Result<LabelListDto>
}