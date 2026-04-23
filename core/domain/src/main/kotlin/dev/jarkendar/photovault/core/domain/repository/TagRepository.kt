package dev.jarkendar.photovault.core.domain.repository

import dev.jarkendar.photovault.core.domain.model.Tag
import kotlinx.coroutines.flow.Flow

interface TagRepository {
    fun observeAll(): Flow<List<Tag>>
}
