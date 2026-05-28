package dev.jarkendar.photovault.feature.gallery.domain.usecase

import dev.jarkendar.photovault.core.domain.model.Category
import dev.jarkendar.photovault.core.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow

class ObserveCategoriesUseCase(private val repository: CategoryRepository) {
    operator fun invoke(): Flow<List<Category>> = repository.observeAll()
}
