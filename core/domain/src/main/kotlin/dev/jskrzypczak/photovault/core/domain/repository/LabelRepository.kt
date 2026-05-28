package dev.jskrzypczak.photovault.core.domain.repository

import dev.jskrzypczak.photovault.core.domain.model.Label
import kotlinx.coroutines.flow.Flow

interface LabelRepository {
    fun observeAll(): Flow<List<Label>>
}