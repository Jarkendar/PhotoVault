package dev.jskrzypczak.photovault.core.network.auth

/**
 * In-memory [TokenStore] for unit tests.
 * Pre-seeded with fake values so that bearer headers are included by default.
 */
class StubTokenStore(
    private var access: String? = "fake-token-stub",
    private var refresh: String? = "fake-refresh-stub",
) : TokenStore {
    override suspend fun accessToken(): String? = access
    override suspend fun refreshToken(): String? = refresh
    override suspend fun save(access: String, refresh: String) {
        this.access = access
        this.refresh = refresh
    }
    override suspend fun clear() {
        access = null
        refresh = null
    }
}
