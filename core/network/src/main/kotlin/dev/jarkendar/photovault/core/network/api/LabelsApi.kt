package dev.jarkendar.photovault.core.network.api

import dev.jarkendar.photovault.core.network.dto.label.LabelListDto

interface LabelsApi {
    suspend fun listLabels(): Result<LabelListDto>
}