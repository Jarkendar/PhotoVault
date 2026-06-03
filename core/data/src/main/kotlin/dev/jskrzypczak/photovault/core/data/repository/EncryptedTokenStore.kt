package dev.jskrzypczak.photovault.core.data.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import dev.jskrzypczak.photovault.core.data.datastore.KEY_ACCESS_TOKEN
import dev.jskrzypczak.photovault.core.data.datastore.KEY_REFRESH_TOKEN
import dev.jskrzypczak.photovault.core.data.datastore.tokenDataStore
import dev.jskrzypczak.photovault.core.data.security.CryptoManager
import dev.jskrzypczak.photovault.core.network.auth.TokenStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

/**
 * Production [TokenStore] implementation.
 *
 * Tokens are encrypted with AES-GCM using a key held in the Android Keystore (via [CryptoManager])
 * and persisted as Base64 blobs in a private DataStore file. This replaces the deprecated
 * EncryptedSharedPreferences / EncryptedFile APIs from androidx.security-crypto.
 */
internal class EncryptedTokenStore(
    private val context: Context,
    private val crypto: CryptoManager,
) : TokenStore {

    /**
     * Emits `true` while both an access and a refresh token are present in the store.
     * Used by [AuthRepositoryImpl] to derive the initial [AuthState] on cold start.
     */
    val hasTokens: Flow<Boolean> = context.tokenDataStore.data.map { prefs ->
        prefs[KEY_ACCESS_TOKEN] != null && prefs[KEY_REFRESH_TOKEN] != null
    }

    override suspend fun accessToken(): String? {
        val encoded = context.tokenDataStore.data.first()[KEY_ACCESS_TOKEN] ?: return null
        return runCatching { crypto.decrypt(encoded) }.getOrNull()
    }

    override suspend fun refreshToken(): String? {
        val encoded = context.tokenDataStore.data.first()[KEY_REFRESH_TOKEN] ?: return null
        return runCatching { crypto.decrypt(encoded) }.getOrNull()
    }

    override suspend fun save(access: String, refresh: String) {
        val encAccess = crypto.encrypt(access)
        val encRefresh = crypto.encrypt(refresh)
        context.tokenDataStore.edit { prefs ->
            prefs[KEY_ACCESS_TOKEN] = encAccess
            prefs[KEY_REFRESH_TOKEN] = encRefresh
        }
    }

    override suspend fun clear() {
        context.tokenDataStore.edit { prefs ->
            prefs.remove(KEY_ACCESS_TOKEN)
            prefs.remove(KEY_REFRESH_TOKEN)
        }
    }
}
