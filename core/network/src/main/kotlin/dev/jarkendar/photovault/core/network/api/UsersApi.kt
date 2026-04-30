package dev.jarkendar.photovault.core.network.api

import dev.jarkendar.photovault.core.network.dto.user.UserListDto

interface UsersApi {
    suspend fun listUsers(): Result<UserListDto>
}