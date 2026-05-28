package dev.jarkendar.photovault.core.network.dto.user

import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val id: String,
    val username: String,
    val displayName: String,
)

@Serializable
data class UserRefDto(
    val id: String,
    val displayName: String,
)

@Serializable
data class UserListDto(
    val items: List<UserDto>,
)