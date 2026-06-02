package dev.jskrzypczak.photovault.feature.search.domain.usecase

import dev.jskrzypczak.photovault.core.domain.query.SearchQuery
import dev.jskrzypczak.photovault.core.domain.repository.PhotoRepository

class CountPhotosUseCase(private val repository: PhotoRepository) {
    suspend operator fun invoke(query: SearchQuery): Result<Int> =
        repository.count(query)
}
