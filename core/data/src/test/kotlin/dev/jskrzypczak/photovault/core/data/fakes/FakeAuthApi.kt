package dev.jskrzypczak.photovault.core.data.fakes

import dev.jskrzypczak.photovault.core.network.api.AuthApi
import dev.jskrzypczak.photovault.core.network.dto.auth.AuthResponseDto
import dev.jskrzypczak.photovault.core.network.dto.auth.LoginRequestDto
import dev.jskrzypczak.photovault.core.network.dto.auth.RefreshRequestDto
import dev.jskrzypczak.photovault.core.network.dto.user.UserDto

class FakeAuthApi : AuthApi {
    var loginResult: Result<AuthResponseDto> = Result.success(
        AuthResponseDto(
            accessToken = "test-access",
            refreshToken = "test-refresh",
            user = UserDto(id = "user-1", username = "jarek", displayName = "Jarek"),
        ),
    )
    var meResult: Result<UserDto> = Result.success(
        UserDto(id = "user-1", username = "jarek", displayName = "Jarek"),
    )
    var logoutResult: Result<Unit> = Result.success(Unit)

    override suspend fun login(request: LoginRequestDto): Result<AuthResponseDto> = loginResult
    override suspend fun refresh(request: RefreshRequestDto): Result<AuthResponseDto> = error("not used in these tests")
    override suspend fun logout(): Result<Unit> = logoutResult
    override suspend fun me(): Result<UserDto> = meResult
}
