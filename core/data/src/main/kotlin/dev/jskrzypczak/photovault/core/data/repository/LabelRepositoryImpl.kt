package dev.jarkendar.photovault.core.data.repository

import dev.jarkendar.photovault.core.common.AppDispatchers
import dev.jarkendar.photovault.core.data.mapper.database.toDomain
import dev.jarkendar.photovault.core.database.dao.LabelDao
import dev.jarkendar.photovault.core.domain.model.Label
import dev.jarkendar.photovault.core.domain.repository.LabelRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

class LabelRepositoryImpl(
    private val labelDao: LabelDao,
    private val dispatchers: AppDispatchers,
) : LabelRepository {
    override fun observeAll(): Flow<List<Label>> =
        labelDao.observeAll()
            .map { entities -> entities.map { it.toDomain() } }
            .flowOn(dispatchers.default)
}