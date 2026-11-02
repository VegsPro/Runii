package com.runii.vegs

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.runii.vegs.ui.screens.GameScreen
import com.runii.vegs.ui.screens.MainMenuScreen
import com.runii.vegs.ui.screens.SessionsScreen
import com.runii.vegs.ui.screens.SettingsScreen
import com.runii.vegs.ui.screens.WorldSelectScreen
import com.runii.vegs.ui.theme.RuniiTheme
import com.runii.vegs.viewmodel.SettingsViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val app = application as RuniiApp

        setContent {
            val settingsVm: SettingsViewModel = viewModel(
                factory = SettingsViewModel.Factory(app.settingsRepository)
            )
            val theme by settingsVm.theme.collectAsState()

            RuniiTheme(themeMode = theme) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val navController = rememberNavController()

                    NavHost(
                        navController = navController,
                        startDestination = "menu"
                    ) {
                        composable("menu") {
                            MainMenuScreen(
                                onSessions = { navController.navigate("sessions") },
                                onSettings = { navController.navigate("settings") },
                                onExit = { finish() }
                            )
                        }
                        composable("sessions") {
                            SessionsScreen(
                                onBack = { navController.popBackStack() },
                                onNewSession = { navController.navigate("worlds") },
                                onOpenSession = { id ->
                                    navController.navigate("game/$id")
                                }
                            )
                        }
                        composable("worlds") {
                            WorldSelectScreen(
                                onBack = { navController.popBackStack() },
                                onSelect = { templateId ->
                                    // create happens inside, then navigate
                                    navController.navigate("sessions") {
                                        popUpTo("sessions") { inclusive = false }
                                    }
                                },
                                onCreated = { sessionId ->
                                    navController.navigate("game/$sessionId") {
                                        popUpTo("menu")
                                    }
                                }
                            )
                        }
                        composable("settings") {
                            SettingsScreen(
                                onBack = { navController.popBackStack() }
                            )
                        }
                        composable(
                            "game/{sessionId}",
                            arguments = listOf(navArgument("sessionId") { type = NavType.StringType })
                        ) { backStack ->
                            val sessionId = backStack.arguments?.getString("sessionId") ?: return@composable
                            GameScreen(
                                sessionId = sessionId,
                                onBack = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}
