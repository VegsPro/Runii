package com.runii.vegs.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
data class Currency(
    val name: String,
    val amount: Long = 0,
    val symbol: String = ""
)

@Serializable
data class Item(
    val id: String,
    val name: String,
    val description: String = "",
    val quantity: Int = 1,
    val type: String = "misc", // weapon, armor, consumable, quest, misc
    val value: Long = 0, // in primary currency
    val rarity: String = "common" // common, uncommon, rare, epic, legendary
)

@Serializable
data class InventoryState(
    val currencies: List<Currency> = listOf(
        Currency("Золото", 0, "💰"),
        Currency("Серебро", 0, "🥈"),
        Currency("Медь", 0, "🥉")
    ),
    val items: List<Item> = emptyList()
)

@Serializable
data class Message(
    val id: String,
    val role: String, // "user", "assistant", "system", "event"
    val content: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "sessions")
data class GameSession(
    @PrimaryKey val id: String,
    val title: String,
    val templateId: String,
    val templateName: String,
    val createdAt: Long,
    val updatedAt: Long,
    val historyJson: String, // List<Message> as JSON
    val inventoryJson: String, // InventoryState as JSON
    val contextSummary: String = "", // for AI long-term memory
    val playerName: String = "Искатель",
    val isActive: Boolean = true
)

@Serializable
data class WorldTemplate(
    val id: String,
    val name: String,
    val description: String,
    val genre: String,
    val startingPrompt: String,
    val iconEmoji: String,
    val tags: List<String> = emptyList()
)

object WorldTemplates {
    val all: List<WorldTemplate> = listOf(
        WorldTemplate(
            id = "forgotten_realms",
            name = "Забытые Королевства",
            description = "Классический фэнтези-мир с драконами, магией и древними руинами. Начни как авантюрист в городе Невервинтер.",
            genre = "Высокое фэнтези",
            startingPrompt = "Ты — Мастер Подземелий. Игрок только что прибыл в город Невервинтер. Создай атмосферное вступление, опиши окружение и предложи первые зацепки к приключению.",
            iconEmoji = "🏰",
            tags = listOf("фэнтези", "классика", "dnd")
        ),
        WorldTemplate(
            id = "cyberpunk_night",
            name = "Ночной Неон",
            description = "Киберпанк 2077-style. Мегаполис, корпорации, импланты и уличная война. Ты — нетраннер на грани.",
            genre = "Киберпанк",
            startingPrompt = "Ты — Мастер. Мир киберпанка, город Найт-Сити. Игрок — нетраннер. Начни сцену в дождливом переулке после неудачного джоба.",
            iconEmoji = "🌃",
            tags = listOf("киберпанк", "sci-fi", "нуар")
        ),
        WorldTemplate(
            id = "postapo_wasteland",
            name = "Пустошь",
            description = "Постядерный мир. Выживание, мутанты, фракции. Ищи припасы и союзников.",
            genre = "Постапокалипсис",
            startingPrompt = "Ты — Мастер постапокалиптического мира. Игрок просыпается в руинах убежища. Опиши жестокий мир и первые угрозы.",
            iconEmoji = "☢️",
            tags = listOf("постапок", "выживание")
        ),
        WorldTemplate(
            id = "space_opera",
            name = "Звёздный Путь",
            description = "Космическая опера. Корабль, экипаж, неизведанные планеты и древние цивилизации.",
            genre = "Космос",
            startingPrompt = "Ты — Мастер космической оперы. Игрок — капитан небольшого корабля. Начни с прибытия на пограничную станцию.",
            iconEmoji = "🚀",
            tags = listOf("космос", "sci-fi")
        ),
        WorldTemplate(
            id = "lovecraft",
            name = "Тени Аркхема",
            description = "Лавкрафтианский хоррор. Древние боги, безумие и запретные знания в 1920-х.",
            genre = "Хоррор",
            startingPrompt = "Ты — Мастер космического ужаса. Игрок — частный детектив в Аркхеме. Начни с письма о пропавшем профессоре.",
            iconEmoji = "🐙",
            tags = listOf("хоррор", "лавкрафт")
        ),
        WorldTemplate(
            id = "pirate_seas",
            name = "Пиратские Моря",
            description = "Эпоха паруса, карибские острова, сокровища и морские чудовища.",
            genre = "Приключения",
            startingPrompt = "Ты — Мастер пиратских приключений. Игрок — молодой матрос на борту «Чёрной Вдовы». Начни с шторма и мятежа.",
            iconEmoji = "🏴‍☠️",
            tags = listOf("пираты", "приключения")
        ),
        WorldTemplate(
            id = "steampunk_empire",
            name = "Империя Пара",
            description = "Стимпанк. Дирижабли, механики, алхимия и классовая борьба в викторианском мире.",
            genre = "Стимпанк",
            startingPrompt = "Ты — Мастер стимпанк-мира. Игрок — изобретатель в столице Империи. Начни с демонстрации нового устройства, которая идёт не по плану.",
            iconEmoji = "⚙️",
            tags = listOf("стимпанк", "виктория")
        ),
        WorldTemplate(
            id = "dark_souls_like",
            name = "Проклятые Земли",
            description = "Мрачное фэнтези в духе Dark Souls. Смерть, боссы, потерянная история.",
            genre = "Тёмное фэнтези",
            startingPrompt = "Ты — Мастер мрачного мира. Игрок просыпается в тюрьме забвения. Опиши безысходность и первый выход в руины.",
            iconEmoji = "🔥",
            tags = listOf("дарк-фэнтези", "souls-like")
        ),
        WorldTemplate(
            id = "wild_west",
            name = "Дикий Запад",
            description = "Классический вестерн. Салуны, бандиты, золотая лихорадка и дуэли.",
            genre = "Вестерн",
            startingPrompt = "Ты — Мастер Дикого Запада. Игрок прибывает в пыльный городок Тумстоун. Начни с сцены у салуна.",
            iconEmoji = "🤠",
            tags = listOf("вестерн", "ковбои")
        ),
        WorldTemplate(
            id = "anime_isekai",
            name = "Другой Мир",
            description = "Исекай. Ты попал в мир меча и магии с системой уровней и навыками.",
            genre = "Исекай / Аниме",
            startingPrompt = "Ты — Мастер исекай. Игрок только что перенёсся в другой мир. Опиши пробуждение и появление интерфейса системы.",
            iconEmoji = "🗡️",
            tags = listOf("исекай", "аниме", "rpg")
        ),
        WorldTemplate(
            id = "zombie_apocalypse",
            name = "Живые Мертвецы",
            description = "Зомби-апокалипсис. Выживание, укрепления, человеческая природа.",
            genre = "Зомби",
            startingPrompt = "Ты — Мастер зомби-апокалипсиса. Игрок слышит сирены и видит первые новости. Начни с первых часов хаоса.",
            iconEmoji = "🧟",
            tags = listOf("зомби", "выживание")
        ),
        WorldTemplate(
            id = "mythic_greece",
            name = "Олимп",
            description = "Мифологическая Греция. Боги, герои, чудовища и судьба.",
            genre = "Мифология",
            startingPrompt = "Ты — Мастер греческих мифов. Игрок — молодой герой, которого призвали оракулы. Начни с видения от Афины.",
            iconEmoji = "⚡",
            tags = listOf("мифы", "греция")
        ),
        WorldTemplate(
            id = "noir_detective",
            name = "Город Теней",
            description = "Нуар-детектив 1940-х. Дождь, сигареты, предательство и сложные дела.",
            genre = "Нуар",
            startingPrompt = "Ты — Мастер нуара. Игрок — частный сыщик в дождливом городе. Клиент заходит в офис с таинственным делом.",
            iconEmoji = "🕵️",
            tags = listOf("нуар", "детектив")
        ),
        WorldTemplate(
            id = "fairy_tale",
            name = "Сказочный Лес",
            description = "Тёмные сказки. Лес, ведьмы, сделки и потерянные пути.",
            genre = "Сказка",
            startingPrompt = "Ты — Мастер тёмных сказок. Игрок заблудился в зачарованном лесу. Начни с встречи с говорящим вороном.",
            iconEmoji = "🌲",
            tags = listOf("сказка", "фэнтези")
        ),
        WorldTemplate(
            id = "mecha_war",
            name = "Война Меха",
            description = "Гигантские роботы, война фракций, пилоты и тактика.",
            genre = "Меха",
            startingPrompt = "Ты — Мастер меха-войны. Игрок — молодой пилот на своём первом боевом вылете. Начни с брифинга.",
            iconEmoji = "🤖",
            tags = listOf("меха", "война")
        ),
        WorldTemplate(
            id = "vampire_court",
            name = "Кровавый Двор",
            description = "Вампирская политика. Интриги, кровь, древние кланы в современном мире.",
            genre = "Урбан-фэнтези",
            startingPrompt = "Ты — Мастер вампирского мира. Игрок только что был Обращён. Начни с пробуждения в особняке клана.",
            iconEmoji = "🦇",
            tags = listOf("вампиры", "интриги")
        ),
        WorldTemplate(
            id = "samurai_japan",
            name = "Путь Воина",
            description = "Феодальная Япония. Самураи, честь, ниндзя и сёгунат.",
            genre = "Историческое",
            startingPrompt = "Ты — Мастер самурайского эпоса. Игрок — ронин после падения господина. Начни с дороги в Киото.",
            iconEmoji = "⛩️",
            tags = listOf("самураи", "япония")
        ),
        WorldTemplate(
            id = "alchemy_academy",
            name = "Академия Алхимии",
            description = "Магическая школа. Учёба, соперничество, запретные эксперименты.",
            genre = "Магическая академия",
            startingPrompt = "Ты — Мастер академии магии. Игрок — новый студент. Начни с церемонии поступления и распределения.",
            iconEmoji = "🧪",
            tags = listOf("академия", "магия")
        ),
        WorldTemplate(
            id = "underdark",
            name = "Подземье",
            description = "Подземный мир. Дроу, иллитиды, грибные леса и вечная тьма.",
            genre = "Подземье",
            startingPrompt = "Ты — Мастер Подземья. Игрок — изгнанник дроу. Начни с побега из дома и первых шагов в диких туннелях.",
            iconEmoji = "🕳️",
            tags = listOf("дроу", "подземье")
        ),
        WorldTemplate(
            id = "time_travel",
            name = "Парадоксы Времени",
            description = "Путешествия во времени. Изменение истории, парадоксы и хранители.",
            genre = "Sci-fi",
            startingPrompt = "Ты — Мастер временных парадоксов. Игрок активировал устройство и оказался не в своём времени. Начни с шока осознания.",
            iconEmoji = "⏳",
            tags = listOf("время", "sci-fi")
        ),
        WorldTemplate(
            id = "custom_blank",
            name = "Чистый Лист",
            description = "Полностью свободный мир. Нейросеть создаст всё с нуля по твоим описаниям.",
            genre = "Свободный",
            startingPrompt = "Ты — Мастер. Мир ещё не определён. Спроси игрока, в каком мире он хочет оказаться, или предложи варианты на основе его первого сообщения. Будь креативным.",
            iconEmoji = "✨",
            tags = listOf("свободный", "кастом")
        )
    )
}
