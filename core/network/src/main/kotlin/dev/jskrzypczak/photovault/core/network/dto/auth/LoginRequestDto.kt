package dev.jarkendar.photovault.core.network.dto.auth

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequestDto(
    val username: String,
    val password: String,
)