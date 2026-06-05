package dev.jskrzypczak.photovault.feature.gallery.domain.usecase

import dev.jskrzypczak.photovault.core.domain.id.CategoryId
import dev.jskrzypczak.photovault.core.domain.repository.CategoryRepository

class ToggleCategoryAutoEnabledUseCase(private val repository: CategoryRepository) {
    suspend operator fun invoke(id: CategoryId, enabled: Boolean): Result<Unit> =
        repository.setAutoEnabled(id, enabled)
}
