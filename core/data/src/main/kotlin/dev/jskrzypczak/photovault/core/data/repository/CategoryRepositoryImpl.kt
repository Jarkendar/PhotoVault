package dev.jskrzypczak.photovault.core.data.repository

import dev.jskrzypczak.photovault.core.common.AppDispatchers
import dev.jskrzypczak.photovault.core.data.mapper.database.toDomain
import dev.jskrzypczak.photovault.core.data.mapper.database.toEntity
import dev.jskrzypczak.photovault.core.data.mapper.network.toDomain
import dev.jskrzypczak.photovault.core.database.dao.CategoryDao
import dev.jskrzypczak.photovault.core.domain.model.Category
import dev.jskrzypczak.photovault.core.domain.repository.CategoryRepository
import dev.jskrzypczak.photovault.core.network.api.CategoriesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class CategoryRepositoryImpl(
    private val categoryDao: CategoryDao,
    private val categoriesApi: CategoriesApi,
    private val dispatchers: AppDispatchers,
) : CategoryRepository {

    override fun observeAll(): Flow<List<Category>> =
        categoryDao.observeAll()
            .map { entities -> entities.map { it.toDomain() } }
            .flowOn(dispatchers.default)

    override suspend fun refresh(): Result<Unit> = withContext(dispatchers.io) {
        runCatching {
            val dto = categoriesApi.listCategories().getOrThrow()
            categoryDao.upsert(dto.items.map { it.toDomain().toEntity() })
        }
    }
}
