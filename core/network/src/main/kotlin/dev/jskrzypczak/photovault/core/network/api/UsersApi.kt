package dev.jskrzypczak.photovault.core.network.api

import dev.jskrzypczak.photovault.core.network.dto.user.UserListDto

interface UsersApi {
    suspend fun listUsers(): Result<UserListDto>
}