package dev.jarkendar.photovault.core.data.fakes

import dev.jarkendar.photovault.core.database.dao.LabelDao
import dev.jarkendar.photovault.core.database.entity.LabelEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeLabelDao : LabelDao {
    val items = MutableStateFlow<List<LabelEntity>>(emptyList())

    override fun observeAll(): Flow<List<LabelEntity>> = items

    override suspend fun upsert(items: List<LabelEntity>) {
        val current = this.items.value.associateBy { it.id }.toMutableMap()
        items.forEach { current[it.id] = it }
        this.items.value = current.values.toList()
    }

    override suspend fun deleteById(id: String) {
        items.value = items.value.filterNot { it.id == id }
    }
}