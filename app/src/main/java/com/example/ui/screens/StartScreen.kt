package com.example.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.GameMode
import com.example.model.MatchLimit
import com.example.util.SoundEffects
import com.example.viewmodel.AppScreen
import com.example.viewmodel.GameViewModel

@Composable
fun StartScreen(
    viewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    var p1NameInput by remember { mutableStateOf(uiState.playerOneName) }
    var p2NameInput by remember { mutableStateOf(uiState.playerTwoName) }

    // Visual gradients and color values for the "Sophisticated Dark" theme
    val sophBackground = Color(0xFF1C1B1F)
    val sophCardBg = Color(0xFF2B2930)
    val sophBorderColor = Color(0xFF49454F)
    
    val p1Color = Color(0xFFD0BCFF) // Light lavender
    val p2Color = Color(0xFFCCC2DC) // Muted lavender gray

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = sophBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(20.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Stat Button at top right
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(
                    onClick = {
                        SoundEffects.playButtonSound()
                        viewModel.navigateTo(AppScreen.STATS)
                    },
                    modifier = Modifier
                        .background(sophCardBg, RoundedCornerShape(12.dp))
                        .border(1.dp, sophBorderColor, RoundedCornerShape(12.dp))
                        .testTag("stats_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.List,
                        contentDescription = "Match History & Statistics",
                        tint = p1Color
                    )
                }
            }

            // Header Banner
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "SOS & XOX\nDUEL",
                fontSize = 44.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                lineHeight = 44.sp,
                fontFamily = FontFamily.Serif,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(vertical = 12.dp)
            )

            Text(
                text = "Ultimate Offline Local Multiplayer Action",
                fontSize = 12.sp,
                color = Color(0xFF938F99),
                fontWeight = FontWeight.Medium,
                letterSpacing = 1.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Player names configuration Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = sophCardBg),
                shape = RoundedCornerShape(24.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, sophBorderColor),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "CHALLENGERS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = p1Color,
                        letterSpacing = 1.5.sp
                    )

                    // Player 1 input
                    OutlinedTextField(
                        value = p1NameInput,
                        onValueChange = {
                            p1NameInput = it
                            viewModel.setPlayerOneName(it)
                        },
                        label = { Text("Player 1 Name", color = Color(0xFF938F99)) },
                        leadingIcon = {
                            Icon(Icons.Default.Person, contentDescription = null, tint = p1Color)
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = p1Color,
                            unfocusedBorderColor = sophBorderColor,
                            focusedTextColor = Color(0xFFE6E1E5),
                            unfocusedTextColor = Color(0xFFE6E1E5),
                            focusedContainerColor = Color(0xFF1C1B1F),
                            unfocusedContainerColor = Color(0xFF1C1B1F)
                        ),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().testTag("p1_name_input")
                    )

                    // Player 2 input
                    OutlinedTextField(
                        value = p2NameInput,
                        onValueChange = {
                            p2NameInput = it
                            viewModel.setPlayerTwoName(it)
                        },
                        label = { Text("Player 2 Name", color = Color(0xFF938F99)) },
                        leadingIcon = {
                            Icon(Icons.Default.Person, contentDescription = null, tint = p2Color)
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = p2Color,
                            unfocusedBorderColor = sophBorderColor,
                            focusedTextColor = Color(0xFFE6E1E5),
                            unfocusedTextColor = Color(0xFFE6E1E5),
                            focusedContainerColor = Color(0xFF1C1B1F),
                            unfocusedContainerColor = Color(0xFF1C1B1F)
                        ),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().testTag("p2_name_input")
                    )
                }
            }

            // Game mode selector Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = sophCardBg),
                shape = RoundedCornerShape(24.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, sophBorderColor),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "GAME MODE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = p2Color,
                        letterSpacing = 1.5.sp
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        GameModeButton(
                            title = "SOS",
                            subtitle = "Place S / O to score",
                            isSelected = uiState.gameMode == GameMode.SOS,
                            accentColor = p1Color,
                            onClick = {
                                SoundEffects.playButtonSound()
                                viewModel.setGameMode(GameMode.SOS)
                            },
                            modifier = Modifier.weight(1f).testTag("select_sos_mode")
                        )

                        GameModeButton(
                            title = "XOX",
                            subtitle = "Place X / O to score",
                            isSelected = uiState.gameMode == GameMode.XOX,
                            accentColor = p2Color,
                            onClick = {
                                SoundEffects.playButtonSound()
                                viewModel.setGameMode(GameMode.XOX)
                            },
                            modifier = Modifier.weight(1f).testTag("select_xox_mode")
                        )
                    }
                }
            }

            // Match Limit Selector Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = sophCardBg),
                shape = RoundedCornerShape(24.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, sophBorderColor),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "MATCH LIMIT",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        letterSpacing = 1.5.sp
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        MatchLimit.values().forEach { limit ->
                            MatchLimitChip(
                                limit = limit,
                                isSelected = uiState.matchLimit == limit,
                                activeBgColor = if (uiState.gameMode == GameMode.SOS) p1Color else p2Color,
                                onClick = {
                                    SoundEffects.playButtonSound()
                                    viewModel.setMatchLimit(limit)
                                },
                                modifier = Modifier.weight(1f).testTag("match_limit_${limit.name.lowercase()}")
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // BIG START GRADIANT BUTTON
            Button(
                onClick = {
                    SoundEffects.playButtonSound()
                    viewModel.startNewMatch()
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .testTag("start_duel_button")
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Brush.horizontalGradient(listOf(p2Color, p1Color))),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = Color(0xFF381E72),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "START Match".uppercase(),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF381E72),
                            letterSpacing = 1.5.sp
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun GameModeButton(
    title: String,
    subtitle: String,
    isSelected: Boolean,
    accentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scale by animateFloatAsState(if (isSelected) 1.05f else 1.0f)
    
    Box(
        modifier = modifier
            .scale(scale)
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) accentColor else Color.Gray.copy(alpha = 0.5f),
                shape = RoundedCornerShape(16.dp)
            )
            .background(
                color = if (isSelected) accentColor.copy(alpha = 0.15f) else Color.Transparent,
                shape = RoundedCornerShape(16.dp)
            )
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = title,
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                color = if (isSelected) accentColor else Color.White
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitle,
                fontSize = 10.sp,
                fontWeight = FontWeight.Normal,
                color = if (isSelected) Color.White else Color.Gray,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun MatchLimitChip(
    limit: MatchLimit,
    isSelected: Boolean,
    activeBgColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) activeBgColor else Color.Gray.copy(alpha = 0.5f),
                shape = RoundedCornerShape(12.dp)
            )
            .background(
                color = if (isSelected) activeBgColor.copy(alpha = 0.2f) else Color.Transparent,
                shape = RoundedCornerShape(12.dp)
            )
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = when (limit) {
                    MatchLimit.BEST_OF_1 -> "1 Round"
                    MatchLimit.BEST_OF_3 -> "BO3"
                    MatchLimit.BEST_OF_5 -> "BO5"
                    MatchLimit.CASUAL -> "Casual"
                },
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) Color.White else Color.Gray,
                textAlign = TextAlign.Center
            )
        }
    }
}
