package com.runii.vegs.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.runii.vegs.data.SettingsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(private val repo: SettingsRepository) : ViewModel() {

    val playerName = repo.playerName.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "Искатель")
    val theme = repo.theme.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "system")
    val creativity = repo.creativity.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.85f)
    val difficulty = repo.difficulty.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "normal")
    val languageStyle = repo.languageStyle.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "epic")
    val showInventory = repo.showInventory.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)
    val autoSave = repo.autoSave.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)
    val maxHistory = repo.maxHistory.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "medium")
    val aiPersonality = repo.aiPersonality.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "balanced")
    val currencySystem = repo.currencySystem.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "classic")
    val enableShops = repo.enableShops.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)
    val enableCombat = repo.enableCombat.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)
    val narrativeLength = repo.narrativeLength.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "medium")

    fun setPlayerName(v: String) = viewModelScope.launch { repo.setPlayerName(v) }
    fun setTheme(v: String) = viewModelScope.launch { repo.setTheme(v) }
    fun setCreativity(v: Float) = viewModelScope.launch { repo.setCreativity(v) }
    fun setDifficulty(v: String) = viewModelScope.launch { repo.setDifficulty(v) }
    fun setLanguageStyle(v: String) = viewModelScope.launch { repo.setLanguageStyle(v) }
    fun setShowInventory(v: Boolean) = viewModelScope.launch { repo.setShowInventory(v) }
    fun setAutoSave(v: Boolean) = viewModelScope.launch { repo.setAutoSave(v) }
    fun setMaxHistory(v: String) = viewModelScope.launch { repo.setMaxHistory(v) }
    fun setAiPersonality(v: String) = viewModelScope.launch { repo.setAiPersonality(v) }
    fun setCurrencySystem(v: String) = viewModelScope.launch { repo.setCurrencySystem(v) }
    fun setEnableShops(v: Boolean) = viewModelScope.launch { repo.setEnableShops(v) }
    fun setEnableCombat(v: Boolean) = viewModelScope.launch { repo.setEnableCombat(v) }
    fun setNarrativeLength(v: String) = viewModelScope.launch { repo.setNarrativeLength(v) }

    class Factory(private val repo: SettingsRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return SettingsViewModel(repo) as T
        }
    }
}
