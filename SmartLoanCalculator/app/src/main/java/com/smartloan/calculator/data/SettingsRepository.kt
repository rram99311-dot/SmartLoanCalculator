package com.smartloan.calculator.data

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

data class AppSettings(val currency: String = "INR", val darkMode: Boolean = false)
@Singleton class SettingsRepository @Inject constructor(@ApplicationContext private val context: Context) {
    private val currency = stringPreferencesKey("currency"); private val dark = booleanPreferencesKey("dark_mode")
    val settings = context.settingsDataStore.data.map { AppSettings(it[currency] ?: "INR", it[dark] ?: false) }
    suspend fun setCurrency(value: String) { context.settingsDataStore.edit { it[currency] = value } }
    suspend fun setDarkMode(value: Boolean) { context.settingsDataStore.edit { it[dark] = value } }
}
