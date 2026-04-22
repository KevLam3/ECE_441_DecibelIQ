package com.example.ece441project.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.themeDataStore by preferencesDataStore(name = "theme_prefs")

object ThemePreferences {

    private val DARK_MODE_KEY = booleanPreferencesKey("dark_mode")
    private val FONT_SIZE_KEY = intPreferencesKey("font_size")
    private val BUTTON_COLOR_KEY = intPreferencesKey("button_color")

    fun isDarkMode(context: Context): Flow<Boolean> =
        context.themeDataStore.data.map { prefs ->
            prefs[DARK_MODE_KEY] ?: false
        }

    suspend fun setDarkMode(context: Context, enabled: Boolean) {
        context.themeDataStore.edit { prefs ->
            prefs[DARK_MODE_KEY] = enabled
        }
    }

    fun fontSize(context: Context): Flow<Int> =
        context.themeDataStore.data.map { prefs ->
            prefs[FONT_SIZE_KEY] ?: 1   // 0 = small, 1 = medium, 2 = large
        }

    suspend fun setFontSize(context: Context, size: Int) {
        context.themeDataStore.edit { prefs ->
            prefs[FONT_SIZE_KEY] = size
        }
    }

    fun buttonColor(context: Context): Flow<Int> =
        context.themeDataStore.data.map { prefs ->
            prefs[BUTTON_COLOR_KEY] ?: 0xFF6750A4.toInt() // default Material3 purple
        }

    suspend fun setButtonColor(context: Context, color: Int) {
        context.themeDataStore.edit { prefs ->
            prefs[BUTTON_COLOR_KEY] = color
        }
    }

}