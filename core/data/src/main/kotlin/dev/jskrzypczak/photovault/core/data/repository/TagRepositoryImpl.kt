package dev.jskrzypczak.photovault.core.data.repository

import dev.jskrzypczak.photovault.core.common.AppDispatchers
import dev.jskrzypczak.photovault.core.data.mapper.database.toDomain
import dev.jskrzypczak.photovault.core.data.mapper.database.toEntity
import dev.jskrzypczak.photovault.core.data.mapper.network.toDomain
import dev.jskrzypczak.photovault.core.database.dao.TagDao
import dev.jskrzypczak.photovault.core.domain.model.Tag
import dev.jskrzypczak.photovault.core.domain.repository.TagRepository
import dev.jskrzypczak.photovault.core.network.api.TagsApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class TagRepositoryImpl(
    private val tagDao: TagDao,
    private val tagsApi: TagsApi,
    private val dispatchers: AppDispatchers,
) : TagRepository {

    override fun observeAll(): Flow<List<Tag>> =
        tagDao.observeAll()
            .map { entities -> entities.map { it.toDomain() } }
            .flowOn(dispatchers.default)

    override suspend fun refresh(): Result<Unit> = withContext(dispatchers.io) {
        runCatching {
            val dto = tagsApi.listTags(usedOnly = true).getOrThrow()
            tagDao.upsert(dto.items.map { it.toDomain().toEntity() })
        }
    }
}
