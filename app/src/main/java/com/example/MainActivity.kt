package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.ui.screens.GameScreen
import com.example.ui.screens.StartScreen
import com.example.ui.screens.StatsHistoryScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.AppScreen
import com.example.viewmodel.GameViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: GameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val uiState by viewModel.uiState.collectAsState()

                Crossfade(
                    targetState = uiState.currentScreen,
                    animationSpec = tween(durationMillis = 350),
                    modifier = Modifier.fillMaxSize()
                ) { screen ->
                    when (screen) {
                        AppScreen.SETUP -> StartScreen(viewModel = viewModel)
                        AppScreen.GAME -> GameScreen(viewModel = viewModel)
                        AppScreen.STATS -> StatsHistoryScreen(viewModel = viewModel)
                    }
                }
            }
        }
    }
}
