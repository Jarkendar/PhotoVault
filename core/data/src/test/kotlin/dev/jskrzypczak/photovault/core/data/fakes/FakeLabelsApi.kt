package dev.jskrzypczak.photovault.core.data.fakes

import dev.jskrzypczak.photovault.core.network.api.LabelsApi
import dev.jskrzypczak.photovault.core.network.dto.label.LabelListDto

class FakeLabelsApi(
    initialList: Result<LabelListDto> = Result.success(LabelListDto(items = emptyList())),
) : LabelsApi {
    var nextListResponse: Result<LabelListDto> = initialList

    override suspend fun listLabels(): Result<LabelListDto> = nextListResponse
}
