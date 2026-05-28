package dev.jskrzypczak.photovault.feature.gallery.domain.usecase

import dev.jskrzypczak.photovault.core.domain.model.Photo
import dev.jskrzypczak.photovault.core.domain.repository.PhotoRepository
import kotlinx.coroutines.flow.Flow

class ObservePhotosUseCase(private val repository: PhotoRepository) {
    operator fun invoke(): Flow<List<Photo>> = repository.observePhotos()
}
