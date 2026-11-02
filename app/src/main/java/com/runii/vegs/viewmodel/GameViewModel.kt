package com.runii.vegs.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.runii.vegs.ai.GeminiService
import com.runii.vegs.data.AppDatabase
import com.runii.vegs.data.GameSession
import com.runii.vegs.data.InventoryState
import com.runii.vegs.data.Message
import com.runii.vegs.data.SettingsRepository
import com.runii.vegs.data.WorldTemplates
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.UUID

data class GameUiState(
    val session: GameSession? = null,
    val messages: List<Message> = emptyList(),
    val inventory: InventoryState = InventoryState(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val isInventoryOpen: Boolean = false
)

class GameViewModel(
    private val db: AppDatabase,
    private val settingsRepo: SettingsRepository,
    private val sessionId: String
) : ViewModel() {

    private val gemini = GeminiService()
    private val json = Json { ignoreUnknownKeys = true }

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    init {
        loadSession()
    }

    private fun loadSession() {
        viewModelScope.launch {
            val session = db.sessionDao().getSession(sessionId) ?: return@launch
            val messages = try {
                json.decodeFromString<List<Message>>(session.historyJson)
            } catch (e: Exception) {
                emptyList()
            }
            val inventory = try {
                json.decodeFromString<InventoryState>(session.inventoryJson)
            } catch (e: Exception) {
                InventoryState()
            }
            _uiState.value = GameUiState(
                session = session,
                messages = messages,
                inventory = inventory
            )

            // If only system message — start the adventure
            if (messages.size <= 1) {
                startAdventure()
            }
        }
    }

    private fun startAdventure() {
        viewModelScope.launch {
            sendToAi("Начни приключение. Опиши начальную сцену и окружение.", isSystemStart = true)
        }
    }

    fun sendMessage(text: String) {
        if (text.isBlank() || _uiState.value.isLoading) return
        viewModelScope.launch {
            sendToAi(text.trim())
        }
    }

    private suspend fun sendToAi(userText: String, isSystemStart: Boolean = false) {
        val current = _uiState.value
        val session = current.session ?: return

        val userMsg = Message(
            id = UUID.randomUUID().toString(),
            role = "user",
            content = userText
        )

        val newMessages = if (isSystemStart) current.messages else current.messages + userMsg
        _uiState.value = current.copy(
            messages = newMessages,
            isLoading = true,
            error = null
        )

        try {
            val template = WorldTemplates.all.find { it.id == session.templateId }
            val creativity = settingsRepo.creativity.first()
            val difficulty = settingsRepo.difficulty.first()
            val languageStyle = settingsRepo.languageStyle.first()
            val aiPersonality = settingsRepo.aiPersonality.first()
            val enableShops = settingsRepo.enableShops.first()
            val enableCombat = settingsRepo.enableCombat.first()
            val narrativeLength = settingsRepo.narrativeLength.first()

            val systemPrompt = gemini.buildSystemPrompt(
                templateName = session.templateName,
                templatePrompt = template?.startingPrompt ?: "Создай интересный мир.",
                playerName = session.playerName,
                inventory = current.inventory,
                difficulty = difficulty,
                languageStyle = languageStyle,
                aiPersonality = aiPersonality,
                enableShops = enableShops,
                enableCombat = enableCombat,
                narrativeLength = narrativeLength,
                contextSummary = session.contextSummary
            )

            val aiText = gemini.generateResponse(
                systemPrompt = systemPrompt,
                history = newMessages.filter { it.role != "system" },
                userMessage = if (isSystemStart) userText else userText,
                temperature = creativity
            )

            val (cleanText, newInventory) = gemini.parseInventoryChanges(aiText, current.inventory)

            val aiMsg = Message(
                id = UUID.randomUUID().toString(),
                role = "assistant",
                content = cleanText
            )

            val finalMessages = newMessages + aiMsg

            // Update summary occasionally
            val newSummary = if (finalMessages.size % 6 == 0) {
                (session.contextSummary + " " + cleanText.take(200)).takeLast(800)
            } else session.contextSummary

            val updatedSession = session.copy(
                historyJson = json.encodeToString(finalMessages),
                inventoryJson = json.encodeToString(newInventory),
                updatedAt = System.currentTimeMillis(),
                contextSummary = newSummary
            )

            db.sessionDao().update(updatedSession)

            _uiState.value = _uiState.value.copy(
                session = updatedSession,
                messages = finalMessages,
                inventory = newInventory,
                isLoading = false
            )
        } catch (e: Exception) {
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                error = e.message ?: "Ошибка нейросети"
            )
        }
    }

    fun toggleInventory() {
        _uiState.value = _uiState.value.copy(isInventoryOpen = !_uiState.value.isInventoryOpen)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    class Factory(
        private val db: AppDatabase,
        private val settingsRepo: SettingsRepository,
        private val sessionId: String
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return GameViewModel(db, settingsRepo, sessionId) as T
        }
    }
}
