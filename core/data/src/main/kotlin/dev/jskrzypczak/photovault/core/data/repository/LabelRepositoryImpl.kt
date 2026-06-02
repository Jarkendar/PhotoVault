package dev.jskrzypczak.photovault.core.data.repository

import dev.jskrzypczak.photovault.core.common.AppDispatchers
import dev.jskrzypczak.photovault.core.data.mapper.database.toDomain
import dev.jskrzypczak.photovault.core.data.mapper.database.toEntity
import dev.jskrzypczak.photovault.core.data.mapper.network.toDomain
import dev.jskrzypczak.photovault.core.database.dao.LabelDao
import dev.jskrzypczak.photovault.core.domain.model.Label
import dev.jskrzypczak.photovault.core.domain.repository.LabelRepository
import dev.jskrzypczak.photovault.core.network.api.LabelsApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class LabelRepositoryImpl(
    private val labelDao: LabelDao,
    private val labelsApi: LabelsApi,
    private val dispatchers: AppDispatchers,
) : LabelRepository {

    override fun observeAll(): Flow<List<Label>> =
        labelDao.observeAll()
            .map { entities -> entities.map { it.toDomain() } }
            .flowOn(dispatchers.default)

    override suspend fun refresh(): Result<Unit> = withContext(dispatchers.io) {
        runCatching {
            val dto = labelsApi.listLabels().getOrThrow()
            labelDao.upsert(dto.items.map { it.toDomain().toEntity() })
        }
    }
}
