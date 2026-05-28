package dev.jarkendar.photovault.feature.gallery.domain.usecase

import dev.jarkendar.photovault.core.domain.id.PhotoId
import dev.jarkendar.photovault.core.domain.repository.PhotoRepository

class ToggleFavoriteUseCase(private val repository: PhotoRepository) {
    suspend operator fun invoke(id: PhotoId): Result<Unit> = repository.toggleFavorite(id)
}
