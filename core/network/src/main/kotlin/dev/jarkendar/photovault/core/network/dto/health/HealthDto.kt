package dev.jarkendar.photovault.core.network.dto.health

import kotlinx.serialization.Serializable

@Serializable
data class HealthDto(
    val status: String,
    val version: String,
)