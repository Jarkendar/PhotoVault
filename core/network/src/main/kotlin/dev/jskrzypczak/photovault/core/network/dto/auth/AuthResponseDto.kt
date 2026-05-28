package dev.jarkendar.photovault.core.network.dto.auth

import dev.jarkendar.photovault.core.network.dto.user.UserDto
import kotlinx.serialization.Serializable

@Serializable
data class AuthResponseDto(
    val accessToken: String,
    val refreshToken: String,
    val user: UserDto,
)