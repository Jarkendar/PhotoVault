package dev.jarkendar.photovault.core.domain.repository

import dev.jarkendar.photovault.core.domain.model.Label
import kotlinx.coroutines.flow.Flow

interface LabelRepository {
    fun observeAll(): Flow<List<Label>>
}