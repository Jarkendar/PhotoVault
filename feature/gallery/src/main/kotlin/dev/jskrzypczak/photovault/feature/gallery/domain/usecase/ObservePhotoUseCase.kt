package dev.jskrzypczak.photovault.feature.gallery.domain.usecase

import dev.jskrzypczak.photovault.core.domain.id.PhotoId
import dev.jskrzypczak.photovault.core.domain.model.Photo
import dev.jskrzypczak.photovault.core.domain.repository.PhotoRepository
import kotlinx.coroutines.flow.Flow

class ObservePhotoUseCase(private val repository: PhotoRepository) {
    operator fun invoke(id: PhotoId): Flow<Photo?> = repository.observePhoto(id)
}
