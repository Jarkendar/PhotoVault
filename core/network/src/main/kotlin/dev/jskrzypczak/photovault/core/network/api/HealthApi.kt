package dev.jskrzypczak.photovault.core.network.api

import dev.jskrzypczak.photovault.core.network.dto.health.HealthDto

interface HealthApi {
    suspend fun getHealth(): Result<HealthDto>
}