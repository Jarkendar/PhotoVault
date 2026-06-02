package dev.jskrzypczak.photovault.feature.search.domain.usecase

import dev.jskrzypczak.photovault.core.domain.model.Photo
import dev.jskrzypczak.photovault.core.domain.query.SearchQuery
import dev.jskrzypczak.photovault.core.domain.repository.PhotoRepository

class SearchPhotosUseCase(private val repository: PhotoRepository) {
    suspend operator fun invoke(query: SearchQuery): Result<List<Photo>> =
        repository.search(query)
}
