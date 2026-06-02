package dev.jskrzypczak.photovault.feature.search.domain.usecase

import dev.jskrzypczak.photovault.core.domain.model.Tag
import dev.jskrzypczak.photovault.core.domain.repository.TagRepository
import kotlinx.coroutines.flow.Flow

class ObserveTagsUseCase(private val repository: TagRepository) {
    operator fun invoke(): Flow<List<Tag>> = repository.observeAll()
}
