package dev.jarkendar.photovault.core.network.auth

interface AuthTokenProvider {
    suspend fun getAccessToken(): String?
    suspend fun getRefreshToken(): String?
}