package dev.jskrzypczak.photovault.feature.gallery.domain.usecase

import dev.jskrzypczak.photovault.core.domain.repository.PhotoRepository

class RefreshGalleryUseCase(private val repository: PhotoRepository) {
    suspend operator fun invoke(): Result<Unit> = repository.refreshGallery()
}
