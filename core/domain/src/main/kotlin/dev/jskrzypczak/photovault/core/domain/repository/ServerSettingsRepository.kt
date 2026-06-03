package dev.jskrzypczak.photovault.core.domain.repository

import kotlinx.coroutines.flow.Flow

interface ServerSettingsRepository {
    /** Emits the current server base URL (e.g. "http://pi.tailnet.ts.net:8080/v1/"). */
    val serverUrl: Flow<String>

    /** Persists a new server base URL. */
    suspend fun setServerUrl(url: String)

    /** Returns true if the backend /health endpoint responds with 2xx. */
    suspend fun checkHealth(): Boolean
}
