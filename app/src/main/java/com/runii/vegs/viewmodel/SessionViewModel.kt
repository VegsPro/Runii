package com.runii.vegs.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.runii.vegs.data.AppDatabase
import com.runii.vegs.data.GameSession
import com.runii.vegs.data.InventoryState
import com.runii.vegs.data.Message
import com.runii.vegs.data.WorldTemplate
import com.runii.vegs.data.WorldTemplates
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.UUID

class SessionViewModel(private val db: AppDatabase) : ViewModel() {

    private val json = Json { ignoreUnknownKeys = true; prettyPrint = false }

    val sessions: StateFlow<List<GameSession>> = db.sessionDao().getAllSessions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun createSession(template: WorldTemplate, playerName: String, onCreated: (String) -> Unit) {
        viewModelScope.launch {
            val id = UUID.randomUUID().toString()
            val now = System.currentTimeMillis()
            val initialInventory = InventoryState()
            val initialHistory = listOf(
                Message(
                    id = UUID.randomUUID().toString(),
                    role = "system",
                    content = "Сеанс начат. Мир: ${template.name}"
                )
            )
            val session = GameSession(
                id = id,
                title = "${template.name} — $playerName",
                templateId = template.id,
                templateName = template.name,
                createdAt = now,
                updatedAt = now,
                historyJson = json.encodeToString(initialHistory),
                inventoryJson = json.encodeToString(initialInventory),
                playerName = playerName
            )
            db.sessionDao().insert(session)
            onCreated(id)
        }
    }

    fun deleteSession(id: String) {
        viewModelScope.launch {
            db.sessionDao().delete(id)
        }
    }

    fun getTemplate(id: String): WorldTemplate? = WorldTemplates.all.find { it.id == id }

    class Factory(private val db: AppDatabase) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return SessionViewModel(db) as T
        }
    }
}
