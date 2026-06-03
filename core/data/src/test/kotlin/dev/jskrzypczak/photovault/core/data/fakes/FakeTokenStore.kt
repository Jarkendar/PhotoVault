package dev.jskrzypczak.photovault.core.data.fakes

import dev.jskrzypczak.photovault.core.network.auth.TokenStore

class FakeTokenStore(
    private var access: String? = null,
    private var refresh: String? = null,
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
