package dev.jskrzypczak.photovault.feature.search.domain.usecase

import dev.jskrzypczak.photovault.core.domain.model.Label
import dev.jskrzypczak.photovault.core.domain.repository.LabelRepository
import kotlinx.coroutines.flow.Flow

class ObserveLabelsUseCase(private val repository: LabelRepository) {
    operator fun invoke(): Flow<List<Label>> = repository.observeAll()
}
