package dev.jarkendar.photovault.feature.gallery.domain.usecase

import dev.jarkendar.photovault.core.domain.repository.PhotoRepository

class RefreshGalleryUseCase(private val repository: PhotoRepository) {
    suspend operator fun invoke(): Result<Unit> = repository.refreshGallery()
}
