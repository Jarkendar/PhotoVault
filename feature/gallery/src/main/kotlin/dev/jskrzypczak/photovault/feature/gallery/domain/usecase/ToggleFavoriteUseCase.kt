package dev.jskrzypczak.photovault.feature.gallery.domain.usecase

import dev.jskrzypczak.photovault.core.domain.id.PhotoId
import dev.jskrzypczak.photovault.core.domain.repository.PhotoRepository

class ToggleFavoriteUseCase(private val repository: PhotoRepository) {
    suspend operator fun invoke(id: PhotoId): Result<Unit> = repository.toggleFavorite(id)
}
