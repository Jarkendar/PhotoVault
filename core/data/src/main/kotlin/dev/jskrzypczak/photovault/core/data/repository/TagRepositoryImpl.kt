package dev.jarkendar.photovault.core.data.repository

import dev.jarkendar.photovault.core.common.AppDispatchers
import dev.jarkendar.photovault.core.data.mapper.database.toDomain
import dev.jarkendar.photovault.core.database.dao.TagDao
import dev.jarkendar.photovault.core.domain.model.Tag
import dev.jarkendar.photovault.core.domain.repository.TagRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

class TagRepositoryImpl(
    private val tagDao: TagDao,
    private val dispatchers: AppDispatchers,
) : TagRepository {
    override fun observeAll(): Flow<List<Tag>> =
        tagDao.observeAll()
            .map { entities -> entities.map { it.toDomain() } }
            .flowOn(dispatchers.default)
}