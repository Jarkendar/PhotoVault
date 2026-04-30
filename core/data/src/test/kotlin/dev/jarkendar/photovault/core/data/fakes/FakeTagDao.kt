package dev.jarkendar.photovault.core.data.fakes

import dev.jarkendar.photovault.core.database.dao.TagDao
import dev.jarkendar.photovault.core.database.entity.TagEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeTagDao : TagDao {
    val items = MutableStateFlow<List<TagEntity>>(emptyList())

    override fun observeAll(): Flow<List<TagEntity>> = items

    override suspend fun upsert(items: List<TagEntity>) {
        val current = this.items.value.associateBy { it.id }.toMutableMap()
        items.forEach { current[it.id] = it }
        this.items.value = current.values.toList()
    }

    override suspend fun deleteById(id: String) {
        items.value = items.value.filterNot { it.id == id }
    }
}
