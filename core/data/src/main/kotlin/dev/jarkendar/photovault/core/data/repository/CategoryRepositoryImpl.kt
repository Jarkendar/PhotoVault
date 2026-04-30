package dev.jarkendar.photovault.core.data.repository

import dev.jarkendar.photovault.core.common.AppDispatchers
import dev.jarkendar.photovault.core.data.mapper.database.toDomain
import dev.jarkendar.photovault.core.database.dao.CategoryDao
import dev.jarkendar.photovault.core.domain.model.Category
import dev.jarkendar.photovault.core.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

class CategoryRepositoryImpl(
    private val categoryDao: CategoryDao,
    private val dispatchers: AppDispatchers,
) : CategoryRepository {
    override fun observeAll(): Flow<List<Category>> =
        categoryDao.observeAll()
            .map { entities -> entities.map { it.toDomain() } }
            .flowOn(dispatchers.default)
}