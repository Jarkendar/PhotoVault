package dev.jskrzypczak.photovault.core.data.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import dev.jskrzypczak.photovault.core.data.datastore.KEY_SERVER_URL
import dev.jskrzypczak.photovault.core.data.datastore.serverSettingsDataStore
import dev.jskrzypczak.photovault.core.domain.repository.ServerSettingsRepository
import dev.jskrzypczak.photovault.core.network.BaseUrlProvider
import dev.jskrzypczak.photovault.core.network.BuildConfig
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.http.isSuccess
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn

/**
 * Implements both [ServerSettingsRepository] (for the domain / feature layer) and
 * [BaseUrlProvider] (for Ktor's DefaultRequest). The cached value is updated eagerly
 * via the DataStore flow so that [current] never blocks.
 */
class ServerSettingsRepositoryImpl(
    private val context: Context,
    applicationScope: CoroutineScope,
    private val httpClient: () -> HttpClient,
) : ServerSettingsRepository, BaseUrlProvider {

    /** Shared flow; seed = BuildConfig.BASE_URL so first request works before DataStore emits. */
    private val _urlState = context.serverSettingsDataStore.data
        .map { prefs -> prefs[KEY_SERVER_URL] ?: BuildConfig.BASE_URL }
        .onEach { cached = it }
        .stateIn(
            scope = applicationScope,
            started = SharingStarted.Eagerly,
            initialValue = BuildConfig.BASE_URL,
        )

    @Volatile
    private var cached: String = BuildConfig.BASE_URL

    override val serverUrl: Flow<String> = _urlState

    override suspend fun setServerUrl(url: String) {
        context.serverSettingsDataStore.edit { prefs ->
            prefs[KEY_SERVER_URL] = url
        }
    }

    /** Called synchronously by Ktor on every request — returns the last cached URL. */
    override fun current(): String = cached

    /** GETs /health and returns true if the server responds with 2xx. Ignores body content. */
    override suspend fun checkHealth(): Boolean = runCatching {
        httpClient().get("health").status.isSuccess()
    }.getOrDefault(false)
}
