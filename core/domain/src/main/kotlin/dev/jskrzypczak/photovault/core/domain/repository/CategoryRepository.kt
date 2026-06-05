package dev.jskrzypczak.photovault.core.domain.repository

import dev.jskrzypczak.photovault.core.domain.id.CategoryId
import dev.jskrzypczak.photovault.core.domain.model.Category
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {
    fun observeAll(): Flow<List<Category>>
    suspend fun refresh(): Result<Unit>
    suspend fun setAutoEnabled(id: CategoryId, enabled: Boolean): Result<Unit>
}
