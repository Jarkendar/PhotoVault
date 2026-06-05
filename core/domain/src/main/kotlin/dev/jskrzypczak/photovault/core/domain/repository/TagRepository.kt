package dev.jskrzypczak.photovault.core.domain.repository

import dev.jskrzypczak.photovault.core.domain.id.TagId
import dev.jskrzypczak.photovault.core.domain.model.Tag
import kotlinx.coroutines.flow.Flow

interface TagRepository {
    fun observeAll(): Flow<List<Tag>>
    suspend fun refresh(): Result<Unit>
    suspend fun setAutoEnabled(id: TagId, enabled: Boolean): Result<Unit>
}
