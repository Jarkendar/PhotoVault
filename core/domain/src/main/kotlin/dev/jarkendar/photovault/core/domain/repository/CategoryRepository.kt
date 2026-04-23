package dev.jarkendar.photovault.core.domain.repository

import dev.jarkendar.photovault.core.domain.model.Category
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {
    fun observeAll(): Flow<List<Category>>
}
