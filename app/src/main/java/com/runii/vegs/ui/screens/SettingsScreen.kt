package com.runii.vegs.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.runii.vegs.RuniiApp
import com.runii.vegs.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(onBack: () -> Unit) {
    val app = LocalContext.current.applicationContext as RuniiApp
    val vm: SettingsViewModel = viewModel(factory = SettingsViewModel.Factory(app.settingsRepository))

    val playerName by vm.playerName.collectAsState()
    val theme by vm.theme.collectAsState()
    val creativity by vm.creativity.collectAsState()
    val difficulty by vm.difficulty.collectAsState()
    val languageStyle by vm.languageStyle.collectAsState()
    val showInventory by vm.showInventory.collectAsState()
    val autoSave by vm.autoSave.collectAsState()
    val maxHistory by vm.maxHistory.collectAsState()
    val aiPersonality by vm.aiPersonality.collectAsState()
    val currencySystem by vm.currencySystem.collectAsState()
    val enableShops by vm.enableShops.collectAsState()
    val enableCombat by vm.enableCombat.collectAsState()
    val narrativeLength by vm.narrativeLength.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Настройки") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Player name
            SettingSection(title = "Игрок") {
                OutlinedTextField(
                    value = playerName,
                    onValueChange = { vm.setPlayerName(it) },
                    label = { Text("Имя персонажа") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }

            // Theme
            SettingSection(title = "Тема оформления") {
                ChipRow(
                    options = listOf("system" to "Системная", "dark" to "Тёмная", "light" to "Светлая"),
                    selected = theme,
                    onSelect = { vm.setTheme(it) }
                )
            }

            // Creativity
            SettingSection(title = "Креативность нейросети (${String.format("%.2f", creativity)})") {
                Text(
                    text = "Низкая — более предсказуемо. Высокая — больше неожиданных поворотов.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Slider(
                    value = creativity,
                    onValueChange = { vm.setCreativity(it) },
                    valueRange = 0.3f..1.2f,
                    steps = 8
                )
            }

            // Difficulty
            SettingSection(title = "Сложность") {
                ChipRow(
                    options = listOf(
                        "easy" to "Лёгкая",
                        "normal" to "Обычная",
                        "hard" to "Сложная",
                        "deadly" to "Смертельная"
                    ),
                    selected = difficulty,
                    onSelect = { vm.setDifficulty(it) }
                )
            }

            // Language style
            SettingSection(title = "Стиль повествования") {
                ChipRow(
                    options = listOf(
                        "epic" to "Эпичный",
                        "formal" to "Формальный",
                        "casual" to "Разговорный",
                        "humorous" to "С юмором"
                    ),
                    selected = languageStyle,
                    onSelect = { vm.setLanguageStyle(it) }
                )
            }

            // AI Personality
            SettingSection(title = "Характер Мастера") {
                ChipRow(
                    options = listOf(
                        "balanced" to "Сбалансированный",
                        "narrative" to "Рассказчик",
                        "tactical" to "Тактический",
                        "chaotic" to "Хаотичный"
                    ),
                    selected = aiPersonality,
                    onSelect = { vm.setAiPersonality(it) }
                )
            }

            // Narrative length
            SettingSection(title = "Длина ответов") {
                ChipRow(
                    options = listOf(
                        "short" to "Короткие",
                        "medium" to "Средние",
                        "long" to "Длинные"
                    ),
                    selected = narrativeLength,
                    onSelect = { vm.setNarrativeLength(it) }
                )
            }

            // Currency system
            SettingSection(title = "Система валют") {
                ChipRow(
                    options = listOf(
                        "classic" to "Классическая (З/С/М)",
                        "simple" to "Простая (только золото)",
                        "complex" to "Сложная (много валют)"
                    ),
                    selected = currencySystem,
                    onSelect = { vm.setCurrencySystem(it) }
                )
            }

            // Toggles
            SettingSection(title = "Геймплей") {
                SwitchRow("Показывать инвентарь", showInventory) { vm.setShowInventory(it) }
                SwitchRow("Автосохранение", autoSave) { vm.setAutoSave(it) }
                SwitchRow("Магазины и торговцы", enableShops) { vm.setEnableShops(it) }
                SwitchRow("Боевая система", enableCombat) { vm.setEnableCombat(it) }
            }

            // History length
            SettingSection(title = "Контекст истории") {
                ChipRow(
                    options = listOf(
                        "short" to "Короткий",
                        "medium" to "Средний",
                        "long" to "Длинный"
                    ),
                    selected = maxHistory,
                    onSelect = { vm.setMaxHistory(it) }
                )
                Text(
                    text = "Влияет на то, сколько прошлых сообщений нейросеть помнит.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun SettingSection(
    title: String,
    content: @Composable () -> Unit
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(8.dp))
        content()
    }
}

@Composable
private fun ChipRow(
    options: List<Pair<String, String>>,
    selected: String,
    onSelect: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        options.forEach { (value, label) ->
            FilterChip(
                selected = selected == value,
                onClick = { onSelect(value) },
                label = { Text(label) }
            )
        }
    }
}

@Composable
private fun SwitchRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyLarge)
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
