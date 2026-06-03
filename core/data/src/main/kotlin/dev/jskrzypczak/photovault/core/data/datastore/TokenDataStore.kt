package dev.jskrzypczak.photovault.core.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore

internal val Context.tokenDataStore: DataStore<Preferences>
    by preferencesDataStore(name = "auth_tokens")

/** Preference key for the encrypted access token blob. */
internal val KEY_ACCESS_TOKEN = stringPreferencesKey("access_token")

/** Preference key for the encrypted refresh token blob. */
internal val KEY_REFRESH_TOKEN = stringPreferencesKey("refresh_token")
