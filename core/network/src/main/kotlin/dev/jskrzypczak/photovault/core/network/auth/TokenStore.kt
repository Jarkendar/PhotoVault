package dev.jskrzypczak.photovault.core.network.auth

/**
 * Read/write contract for the JWT access + refresh token pair.
 *
 * Implemented by [dev.jskrzypczak.photovault.core.data.repository.EncryptedTokenStore]
 * (DataStore + Android Keystore) in production; by [StubTokenStore] in tests.
 *
 * Mirrors the [dev.jskrzypczak.photovault.core.network.BaseUrlProvider] pattern:
 * the interface lives in :core:network, the implementation lives in :core:data.
 */
interface TokenStore {
    /** Returns the current access token, or null if no session exists. */
    suspend fun accessToken(): String?

    /** Returns the current refresh token, or null if no session exists. */
    suspend fun refreshToken(): String?

    /** Persists a new token pair after a successful login or token refresh. */
    suspend fun save(access: String, refresh: String)

    /** Removes all stored tokens (called on logout or when refresh fails). */
    suspend fun clear()
}
