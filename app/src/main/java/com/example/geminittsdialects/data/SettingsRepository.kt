package com.example.geminittsdialects.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.example.geminittsdialects.models.UserSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsRepository(private val context: Context) {

    private val dataStore = context.dataStore

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val encryptedPrefs = EncryptedSharedPreferences.create(
        context,
        "secure_settings",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    companion object {
        private const val SECURE_API_KEY = "secure_api_key"
        val THEME_PREFERENCE = stringPreferencesKey("theme_preference")
        val DYNAMIC_COLOR = booleanPreferencesKey("dynamic_color")
    }

    val userSettingsFlow: Flow<UserSettings> = dataStore.data
        .map { preferences ->
            UserSettings(
                apiKey = encryptedPrefs.getString(SECURE_API_KEY, "") ?: "",
                themePreference = preferences[THEME_PREFERENCE] ?: "system",
                useDynamicColor = preferences[DYNAMIC_COLOR] ?: true
            )
        }

    suspend fun saveSettings(settings: UserSettings) {
        encryptedPrefs.edit().putString(SECURE_API_KEY, settings.apiKey).apply()

        dataStore.edit { preferences ->
            preferences[THEME_PREFERENCE] = settings.themePreference
            preferences[DYNAMIC_COLOR] = settings.useDynamicColor
        }
    }
}
