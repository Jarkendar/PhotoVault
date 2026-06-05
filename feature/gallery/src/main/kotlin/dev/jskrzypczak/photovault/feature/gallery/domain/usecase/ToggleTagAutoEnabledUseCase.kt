package dev.jskrzypczak.photovault.feature.gallery.domain.usecase

import dev.jskrzypczak.photovault.core.domain.id.TagId
import dev.jskrzypczak.photovault.core.domain.repository.TagRepository

class ToggleTagAutoEnabledUseCase(private val repository: TagRepository) {
    suspend operator fun invoke(id: TagId, enabled: Boolean): Result<Unit> =
        repository.setAutoEnabled(id, enabled)
}
