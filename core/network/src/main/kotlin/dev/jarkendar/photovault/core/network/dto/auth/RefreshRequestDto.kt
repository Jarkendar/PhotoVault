package dev.jarkendar.photovault.core.network.dto.auth

import kotlinx.serialization.Serializable

@Serializable
data class RefreshRequestDto(
    val refreshToken: String,
)