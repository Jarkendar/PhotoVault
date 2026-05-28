package dev.jarkendar.photovault.core.data.fakes

import dev.jarkendar.photovault.core.database.dao.CategoryDao
import dev.jarkendar.photovault.core.database.entity.CategoryEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeCategoryDao : CategoryDao {
    val items = MutableStateFlow<List<CategoryEntity>>(emptyList())

    override fun observeAll(): Flow<List<CategoryEntity>> = items

    override suspend fun upsert(items: List<CategoryEntity>) {
        val current = this.items.value.associateBy { it.id }.toMutableMap()
        items.forEach { current[it.id] = it }
        this.items.value = current.values.toList()
    }

    override suspend fun deleteById(id: String) {
        items.value = items.value.filterNot { it.id == id }
    }
}