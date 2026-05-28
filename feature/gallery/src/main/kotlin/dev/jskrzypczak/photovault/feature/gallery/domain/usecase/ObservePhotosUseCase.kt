package dev.jarkendar.photovault.feature.gallery.domain.usecase

import dev.jarkendar.photovault.core.domain.model.Photo
import dev.jarkendar.photovault.core.domain.repository.PhotoRepository
import kotlinx.coroutines.flow.Flow

class ObservePhotosUseCase(private val repository: PhotoRepository) {
    operator fun invoke(): Flow<List<Photo>> = repository.observePhotos()
}
