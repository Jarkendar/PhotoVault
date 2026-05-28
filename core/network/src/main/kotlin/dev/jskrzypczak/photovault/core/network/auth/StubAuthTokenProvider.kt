package dev.jarkendar.photovault.core.network.auth

class StubAuthTokenProvider : AuthTokenProvider {
    override suspend fun getAccessToken(): String = "fake-token-stub"
    override suspend fun getRefreshToken(): String = "fake-refresh-stub"
}