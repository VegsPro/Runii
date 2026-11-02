package com.runii.vegs.ai

import com.runii.vegs.BuildConfig
import com.runii.vegs.data.InventoryState
import com.runii.vegs.data.Item
import com.runii.vegs.data.Message
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.UUID
import java.util.concurrent.TimeUnit

class GeminiService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(90, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val json = Json { ignoreUnknownKeys = true; isLenient = true }

    private val apiKey = BuildConfig.GEMINI_API_KEY

    // Gemini 1.5 Flash / 2.0 Flash endpoint (free tier friendly)
    private val endpoint = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=$apiKey"

    @Serializable
    data class ContentPart(val text: String)

    @Serializable
    data class Content(val role: String? = null, val parts: List<ContentPart>)

    @Serializable
    data class GenerationConfig(
        val temperature: Float = 0.9f,
        val topP: Float = 0.95f,
        val topK: Int = 40,
        val maxOutputTokens: Int = 2048
    )

    @Serializable
    data class RequestBody(
        val contents: List<Content>,
        val generationConfig: GenerationConfig? = null,
        val systemInstruction: Content? = null
    )

    @Serializable
    data class Candidate(val content: Content? = null)

    @Serializable
    data class ResponseBody(val candidates: List<Candidate>? = null)

    suspend fun generateResponse(
        systemPrompt: String,
        history: List<Message>,
        userMessage: String,
        temperature: Float = 0.85f
    ): String = withContext(Dispatchers.IO) {
        val contents = mutableListOf<Content>()

        // History (limit last messages to fit context)
        val recent = history.takeLast(20)
        for (msg in recent) {
            val role = when (msg.role) {
                "user" -> "user"
                else -> "model"
            }
            contents.add(Content(role = role, parts = listOf(ContentPart(msg.content))))
        }

        contents.add(Content(role = "user", parts = listOf(ContentPart(userMessage))))

        val body = RequestBody(
            contents = contents,
            generationConfig = GenerationConfig(temperature = temperature),
            systemInstruction = Content(parts = listOf(ContentPart(systemPrompt)))
        )

        val requestJson = json.encodeToString(body)

        val request = Request.Builder()
            .url(endpoint)
            .post(requestJson.toRequestBody("application/json".toMediaType()))
            .build()

        val response = client.newCall(request).execute()
        val responseBody = response.body?.string() ?: throw Exception("Empty response")

        if (!response.isSuccessful) {
            throw Exception("API error ${response.code}: $responseBody")
        }

        val parsed = json.decodeFromString<ResponseBody>(responseBody)
        parsed.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
            ?: throw Exception("No content in response")
    }

    fun buildSystemPrompt(
        templateName: String,
        templatePrompt: String,
        playerName: String,
        inventory: InventoryState,
        difficulty: String,
        languageStyle: String,
        aiPersonality: String,
        enableShops: Boolean,
        enableCombat: Boolean,
        narrativeLength: String,
        contextSummary: String
    ): String {
        val inventoryDesc = buildString {
            append("Текущий инвентарь игрока:\n")
            inventory.currencies.forEach {
                append("- ${it.name}: ${it.amount} ${it.symbol}\n")
            }
            if (inventory.items.isEmpty()) {
                append("- Предметы: пусто\n")
            } else {
                inventory.items.forEach {
                    append("- ${it.name} x${it.quantity} (${it.rarity}, ${it.type}): ${it.description}\n")
                }
            }
        }

        val styleGuide = when (languageStyle) {
            "formal" -> "Пиши формально, возвышенным языком."
            "casual" -> "Пиши просто и разговорно."
            "humorous" -> "Добавляй лёгкий юмор и иронию."
            else -> "Пиши эпично, атмосферно, в стиле хорошего фэнтези/приключенческого романа."
        }

        val personality = when (aiPersonality) {
            "narrative" -> "Фокусируйся на красивом повествовании и атмосфере."
            "tactical" -> "Делай акцент на тактике, выборе и последствиях."
            "chaotic" -> "Будь непредсказуемым, добавляй неожиданные повороты."
            else -> "Балансируй повествование, ролевую игру и механику."
        }

        val lengthGuide = when (narrativeLength) {
            "short" -> "Отвечай относительно кратко (2-4 абзаца)."
            "long" -> "Пиши развёрнуто, с деталями (5-8 абзацев)."
            else -> "Оптимальная длина: 3-6 абзацев."
        }

        return """
Ты — опытный Мастер (Dungeon Master / Game Master) для текстовой ролевой игры в стиле D&D.

Мир / Кампания: $templateName
Базовый промпт мира: $templatePrompt

Имя игрока: $playerName
Сложность: $difficulty
$styleGuide
$personality
$lengthGuide

ПРАВИЛА:
1. Всегда оставайся в роли Мастера. Никогда не ломай четвёртую стену без необходимости.
2. Описывай мир, NPC, события ярко и вовлекающе.
3. Когда игрок делает действие — описывай результат, последствия и новые возможности.
4. Управляй инвентарём и валютой. Если выдаёшь/забираешь предметы или деньги — обязательно укажи это в специальном блоке в КОНЦЕ ответа.
5. Формат блока изменений инвентаря (если есть изменения):
[INVENTORY]
ADD_ITEM:название|описание|количество|тип|ценность|редкость
REMOVE_ITEM:название|количество
ADD_CURRENCY:название|количество
REMOVE_CURRENCY:название|количество
[/INVENTORY]
Пример:
[INVENTORY]
ADD_ITEM:Зелье лечения|Восстанавливает 20 HP|1|consumable|15|common
ADD_CURRENCY:Золото|50
[/INVENTORY]

6. Магазины и торговцы: если ${if (enableShops) "включён" else "выключен"} режим магазинов. Когда игрок входит в лавку — опиши ассортимент и цены. Игрок может писать "купить X" или "продать Y".
7. Бой: ${if (enableCombat) "разрешён. Используй простую систему (броски d20 в уме, описывай результат)" else "избегай прямого боя или решай его нарративно"}.
8. Если игрок пишет на русском — отвечай на русском. Всегда на языке игрока.
9. Поддерживай контекст. Краткое резюме предыдущих событий: $contextSummary
10. Не повторяй одни и те же фразы. Развивай сюжет.

$inventoryDesc

Начинай или продолжай игру прямо сейчас.
""".trimIndent()
    }

    fun parseInventoryChanges(response: String, current: InventoryState): Pair<String, InventoryState> {
        val inventoryRegex = Regex("""\[INVENTORY](.*?)\[/INVENTORY]""", RegexOption.DOT_MATCHES_ALL)
        val match = inventoryRegex.find(response)
        if (match == null) {
            return response to current
        }

        val block = match.groupValues[1].trim()
        val cleanResponse = response.replace(inventoryRegex, "").trim()

        var currencies = current.currencies.toMutableList()
        var items = current.items.toMutableList()

        block.lines().forEach { line ->
            val trimmed = line.trim()
            when {
                trimmed.startsWith("ADD_ITEM:") -> {
                    val parts = trimmed.removePrefix("ADD_ITEM:").split("|")
                    if (parts.size >= 2) {
                        val name = parts[0].trim()
                        val desc = parts.getOrNull(1)?.trim() ?: ""
                        val qty = parts.getOrNull(2)?.trim()?.toIntOrNull() ?: 1
                        val type = parts.getOrNull(3)?.trim() ?: "misc"
                        val value = parts.getOrNull(4)?.trim()?.toLongOrNull() ?: 0
                        val rarity = parts.getOrNull(5)?.trim() ?: "common"
                        val existing = items.indexOfFirst { it.name.equals(name, ignoreCase = true) }
                        if (existing >= 0) {
                            val old = items[existing]
                            items[existing] = old.copy(quantity = old.quantity + qty)
                        } else {
                            items.add(Item(UUID.randomUUID().toString(), name, desc, qty, type, value, rarity))
                        }
                    }
                }
                trimmed.startsWith("REMOVE_ITEM:") -> {
                    val parts = trimmed.removePrefix("REMOVE_ITEM:").split("|")
                    if (parts.isNotEmpty()) {
                        val name = parts[0].trim()
                        val qty = parts.getOrNull(1)?.trim()?.toIntOrNull() ?: 1
                        val idx = items.indexOfFirst { it.name.equals(name, ignoreCase = true) }
                        if (idx >= 0) {
                            val old = items[idx]
                            if (old.quantity <= qty) items.removeAt(idx)
                            else items[idx] = old.copy(quantity = old.quantity - qty)
                        }
                    }
                }
                trimmed.startsWith("ADD_CURRENCY:") -> {
                    val parts = trimmed.removePrefix("ADD_CURRENCY:").split("|")
                    if (parts.size >= 2) {
                        val name = parts[0].trim()
                        val amount = parts[1].trim().toLongOrNull() ?: 0
                        val idx = currencies.indexOfFirst { it.name.equals(name, ignoreCase = true) }
                        if (idx >= 0) {
                            currencies[idx] = currencies[idx].copy(amount = currencies[idx].amount + amount)
                        } else {
                            currencies.add(com.runii.vegs.data.Currency(name, amount))
                        }
                    }
                }
                trimmed.startsWith("REMOVE_CURRENCY:") -> {
                    val parts = trimmed.removePrefix("REMOVE_CURRENCY:").split("|")
                    if (parts.size >= 2) {
                        val name = parts[0].trim()
                        val amount = parts[1].trim().toLongOrNull() ?: 0
                        val idx = currencies.indexOfFirst { it.name.equals(name, ignoreCase = true) }
                        if (idx >= 0) {
                            currencies[idx] = currencies[idx].copy(amount = maxOf(0, currencies[idx].amount - amount))
                        }
                    }
                }
            }
        }

        return cleanResponse to InventoryState(currencies, items)
    }
}
