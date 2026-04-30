package dev.jarkendar.photovault.core.network.api

import dev.jarkendar.photovault.core.network.dto.health.HealthDto

interface HealthApi {
    suspend fun getHealth(): Result<HealthDto>
}