package dev.jarkendar.photovault.core.network.api

import dev.jarkendar.photovault.core.network.dto.auth.AuthResponseDto
import dev.jarkendar.photovault.core.network.dto.auth.LoginRequestDto
import dev.jarkendar.photovault.core.network.dto.auth.RefreshRequestDto
import dev.jarkendar.photovault.core.network.dto.user.UserDto

interface AuthApi {
    suspend fun login(request: LoginRequestDto): Result<AuthResponseDto>
    suspend fun refresh(request: RefreshRequestDto): Result<AuthResponseDto>
    suspend fun logout(): Result<Unit>
    suspend fun me(): Result<UserDto>
}