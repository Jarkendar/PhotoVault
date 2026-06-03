package dev.jskrzypczak.photovault.core.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore

internal val Context.serverSettingsDataStore: DataStore<Preferences>
    by preferencesDataStore(name = "server_settings")

internal val KEY_SERVER_URL = stringPreferencesKey("server_url")
