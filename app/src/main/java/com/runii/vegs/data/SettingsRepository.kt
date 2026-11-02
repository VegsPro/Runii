package com.runii.vegs.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsRepository(private val context: Context) {

    private val playerNameKey = stringPreferencesKey("player_name")
    private val themeKey = stringPreferencesKey("theme") // system, dark, light
    private val creativityKey = floatPreferencesKey("creativity") // 0.0 - 1.0 temperature
    private val difficultyKey = stringPreferencesKey("difficulty") // easy, normal, hard, deadly
    private val languageStyleKey = stringPreferencesKey("language_style") // formal, casual, epic, humorous
    private val showInventoryKey = booleanPreferencesKey("show_inventory")
    private val autoSaveKey = booleanPreferencesKey("auto_save")
    private val maxHistoryKey = stringPreferencesKey("max_history") // short, medium, long
    private val aiPersonalityKey = stringPreferencesKey("ai_personality") // balanced, narrative, tactical, chaotic
    private val currencySystemKey = stringPreferencesKey("currency_system") // classic, simple, complex
    private val enableShopsKey = booleanPreferencesKey("enable_shops")
    private val enableCombatKey = booleanPreferencesKey("enable_combat")
    private val narrativeLengthKey = stringPreferencesKey("narrative_length") // short, medium, long

    val playerName: Flow<String> = context.dataStore.data.map { it[playerNameKey] ?: "Искатель" }
    val theme: Flow<String> = context.dataStore.data.map { it[themeKey] ?: "system" }
    val creativity: Flow<Float> = context.dataStore.data.map { it[creativityKey] ?: 0.85f }
    val difficulty: Flow<String> = context.dataStore.data.map { it[difficultyKey] ?: "normal" }
    val languageStyle: Flow<String> = context.dataStore.data.map { it[languageStyleKey] ?: "epic" }
    val showInventory: Flow<Boolean> = context.dataStore.data.map { it[showInventoryKey] ?: true }
    val autoSave: Flow<Boolean> = context.dataStore.data.map { it[autoSaveKey] ?: true }
    val maxHistory: Flow<String> = context.dataStore.data.map { it[maxHistoryKey] ?: "medium" }
    val aiPersonality: Flow<String> = context.dataStore.data.map { it[aiPersonalityKey] ?: "balanced" }
    val currencySystem: Flow<String> = context.dataStore.data.map { it[currencySystemKey] ?: "classic" }
    val enableShops: Flow<Boolean> = context.dataStore.data.map { it[enableShopsKey] ?: true }
    val enableCombat: Flow<Boolean> = context.dataStore.data.map { it[enableCombatKey] ?: true }
    val narrativeLength: Flow<String> = context.dataStore.data.map { it[narrativeLengthKey] ?: "medium" }

    suspend fun setPlayerName(value: String) {
        context.dataStore.edit { it[playerNameKey] = value }
    }
    suspend fun setTheme(value: String) {
        context.dataStore.edit { it[themeKey] = value }
    }
    suspend fun setCreativity(value: Float) {
        context.dataStore.edit { it[creativityKey] = value }
    }
    suspend fun setDifficulty(value: String) {
        context.dataStore.edit { it[difficultyKey] = value }
    }
    suspend fun setLanguageStyle(value: String) {
        context.dataStore.edit { it[languageStyleKey] = value }
    }
    suspend fun setShowInventory(value: Boolean) {
        context.dataStore.edit { it[showInventoryKey] = value }
    }
    suspend fun setAutoSave(value: Boolean) {
        context.dataStore.edit { it[autoSaveKey] = value }
    }
    suspend fun setMaxHistory(value: String) {
        context.dataStore.edit { it[maxHistoryKey] = value }
    }
    suspend fun setAiPersonality(value: String) {
        context.dataStore.edit { it[aiPersonalityKey] = value }
    }
    suspend fun setCurrencySystem(value: String) {
        context.dataStore.edit { it[currencySystemKey] = value }
    }
    suspend fun setEnableShops(value: Boolean) {
        context.dataStore.edit { it[enableShopsKey] = value }
    }
    suspend fun setEnableCombat(value: Boolean) {
        context.dataStore.edit { it[enableCombatKey] = value }
    }
    suspend fun setNarrativeLength(value: String) {
        context.dataStore.edit { it[narrativeLengthKey] = value }
    }
}
