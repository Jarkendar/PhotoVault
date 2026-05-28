package dev.jskrzypczak.photovault.core.network.api

import dev.jskrzypczak.photovault.core.network.dto.auth.AuthResponseDto
import dev.jskrzypczak.photovault.core.network.dto.auth.LoginRequestDto
import dev.jskrzypczak.photovault.core.network.dto.auth.RefreshRequestDto
import dev.jskrzypczak.photovault.core.network.dto.user.UserDto

interface AuthApi {
    suspend fun login(request: LoginRequestDto): Result<AuthResponseDto>
    suspend fun refresh(request: RefreshRequestDto): Result<AuthResponseDto>
    suspend fun logout(): Result<Unit>
    suspend fun me(): Result<UserDto>
}