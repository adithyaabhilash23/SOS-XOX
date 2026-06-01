package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Info
import androidx.compose.ui.draw.scale
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
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
fun GameScreen(
    viewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()
    val hapticFeedback = LocalHapticFeedback.current

    // Sophisticated Dark Color Tokens
    val sophBackground = Color(0xFF1C1B1F)
    val sophCardBg = Color(0xFF2B2930)
    val sophBorderColor = Color(0xFF49454F)
    val sophActiveCellBg = Color(0xFF4A4458)
    
    val p1Color = Color(0xFFD0BCFF) // Soft Lavender (Alex)
    val p2Color = Color(0xFFCCC2DC) // Muted Lavender-Gray (Jordan)
    val lightLavenderText = Color(0xFFE8DEF8)

    // Smooth turn color interpolation
    val animatedTurnColor by animateColorAsState(
        targetValue = if (uiState.activePlayerId == 1) p1Color else p2Color,
        animationSpec = tween(durationMillis = 300)
    )

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = sophBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // HEADER SCOREBOARD: styled precisely like the "Sophisticated Dark" HTML design:
            // pt-8 pb-6 px-6 rounded-b-[40px] shadow-2xl border-b border-[#49454F] bg-[#2B2930]
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(16.dp, RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp)),
                colors = CardDefaults.cardColors(containerColor = sophCardBg),
                shape = RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, sophBorderColor)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp, bottom = 24.dp, start = 20.dp, end = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Utility match details and exits row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = {
                                SoundEffects.playButtonSound()
                                viewModel.navigateTo(AppScreen.SETUP)
                            },
                            modifier = Modifier
                                .background(sophBackground, RoundedCornerShape(12.dp))
                                .border(1.dp, sophBorderColor, RoundedCornerShape(12.dp))
                                .testTag("exit_game_icon")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                                contentDescription = "Leave Match",
                                tint = p1Color
                            )
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${uiState.gameMode.name} DUEL",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = if (uiState.matchLimit == MatchLimit.CASUAL) "Casual Mode" else uiState.matchLimit.displayName,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF938F99)
                            )
                        }

                        IconButton(
                            onClick = { viewModel.restartMatch() },
                            modifier = Modifier
                                .background(sophBackground, RoundedCornerShape(12.dp))
                                .border(1.dp, sophBorderColor, RoundedCornerShape(12.dp))
                                .testTag("restart_match_icon")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Restart Match",
                                tint = p2Color
                            )
                        }
                    }

                    // Score summary row matching: Turn indicator bullet + user text, large font serif score
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        // Player 1 Details Side
                        val p1TurnActive = uiState.activePlayerId == 1
                        Column(
                            horizontalAlignment = Alignment.Start,
                            modifier = Modifier
                                .weight(1.2f)
                                .padding(end = 4.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                modifier = Modifier.padding(bottom = 2.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .background(if (p1TurnActive) p1Color else Color.Transparent, CircleShape)
                                )
                                Text(
                                    text = "P1: ${if (p1TurnActive) "Active" else "Waiting"}",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (p1TurnActive) p1Color else Color(0xFF938F99),
                                    letterSpacing = 1.sp
                                )
                            }
                            Text(
                                text = uiState.playerOneName,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (p1TurnActive) Color.White else Color(0xFF938F99),
                                maxLines = 1
                            )
                            Text(
                                text = String.format("%02d", uiState.playerOneRoundScore),
                                fontSize = 48.sp,
                                fontFamily = FontFamily.Serif,
                                color = Color.White,
                                fontWeight = FontWeight.Normal
                            )
                            Text(
                                text = "${uiState.playerOneMatchWins} Match Wins",
                                fontSize = 10.sp,
                                color = Color(0xFF938F99)
                            )
                        }

                        // Middle Match mode decoration line
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(0.8f).padding(bottom = 6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .background(sophActiveCellBg, RoundedCornerShape(12.dp))
                                    .padding(vertical = 4.dp, horizontal = 10.dp)
                            ) {
                                Text(
                                    text = "${uiState.gameMode.name} Mode",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = lightLavenderText,
                                    letterSpacing = 1.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Box(
                                modifier = Modifier
                                    .width(1.dp)
                                    .height(32.dp)
                                    .background(sophBorderColor)
                            )
                        }

                        // Player 2 Details Side
                        val p2TurnActive = uiState.activePlayerId == 2
                        Column(
                            horizontalAlignment = Alignment.End,
                            modifier = Modifier
                                .weight(1.2f)
                                .padding(start = 4.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                modifier = Modifier.padding(bottom = 2.dp)
                            ) {
                                Text(
                                    text = "P2: ${if (p2TurnActive) "Active" else "Waiting"}",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (p2TurnActive) p2Color else Color(0xFF938F99),
                                    letterSpacing = 1.sp
                                )
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .background(if (p2TurnActive) p2Color else Color.Transparent, CircleShape)
                                )
                            }
                            Text(
                                text = uiState.playerTwoName,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (p2TurnActive) Color.White else Color(0xFF938F99),
                                maxLines = 1,
                                textAlign = TextAlign.End
                            )
                            Text(
                                text = String.format("%02d", uiState.playerTwoRoundScore),
                                fontSize = 48.sp,
                                fontFamily = FontFamily.Serif,
                                color = Color.White,
                                fontWeight = FontWeight.Normal,
                                textAlign = TextAlign.End
                            )
                            Text(
                                text = "${uiState.playerTwoMatchWins} Match Wins",
                                fontSize = 10.sp,
                                color = Color(0xFF938F99),
                                textAlign = TextAlign.End
                            )
                        }
                    }
                }
            }

            // Margin layout container wrapping board & letter choice
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // ACTIVE TURN ALERT CHIP
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp),
                    colors = CardDefaults.cardColors(containerColor = animatedTurnColor.copy(alpha = 0.12f)),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, animatedTurnColor.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(animatedTurnColor, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        val activePlayerName = if (uiState.activePlayerId == 1) uiState.playerOneName else uiState.playerTwoName
                        Text(
                            text = "TURN: $activePlayerName",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            letterSpacing = 1.5.sp
                        )
                    }
                }

                // RESPONSIVE BOARD WRAPPER
                BoxWithConstraints(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(24.dp))
                        .background(sophCardBg)
                        .border(1.dp, sophBorderColor, RoundedCornerShape(24.dp))
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Multi-row Grid System
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        for (row in 0..2) {
                            Row(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                for (col in 0..2) {
                                    val cellIndex = row * 3 + col
                                    val cellState = uiState.board[cellIndex]
                                    
                                    // Check if cell is part of any completed patterns
                                    val p1PatternsOwned = uiState.completedPatterns.filter { it.playerOwnerId == 1 }
                                    val p2PatternsOwned = uiState.completedPatterns.filter { it.playerOwnerId == 2 }
                                    
                                    val partOfP1Match = p1PatternsOwned.any { it.indexes.contains(cellIndex) }
                                    val partOfP2Match = p2PatternsOwned.any { it.indexes.contains(cellIndex) }

                                    BoardCellView(
                                        cellState = cellState,
                                        partOfP1Match = partOfP1Match,
                                        partOfP2Match = partOfP2Match,
                                        p1Color = p1Color,
                                        p2Color = p2Color,
                                        sophCardBg = sophCardBg,
                                        sophBorderColor = sophBorderColor,
                                        sophActiveCellBg = sophActiveCellBg,
                                        onClick = {
                                            viewModel.onCellClicked(cellIndex) {
                                                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                            }
                                        },
                                        modifier = Modifier
                                            .weight(1f)
                                            .fillMaxHeight()
                                            .testTag("board_cell_$cellIndex")
                                    )
                                }
                            }
                        }
                    }
                }

                // LETTER CHOICE SELECTOR
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = sophCardBg),
                    shape = RoundedCornerShape(20.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, sophBorderColor)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        val activePlayerNameSymbol = if (uiState.activePlayerId == 1) uiState.playerOneName else uiState.playerTwoName
                        Text(
                            text = "CHOOSE LETTER TO PLACE ($activePlayerNameSymbol)".uppercase(),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF938F99),
                            letterSpacing = 1.2.sp
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            val firstLetter = if (uiState.gameMode == GameMode.SOS) "S" else "X"
                            val secondLetter = "O"

                            // S / X chip button
                            SophisticatedLetterButton(
                                letterSymbol = firstLetter,
                                isSelected = uiState.selectedLetter == firstLetter,
                                accentColor = animatedTurnColor,
                                p1Color = p1Color,
                                p2Color = p2Color,
                                onClick = {
                                    SoundEffects.playButtonSound()
                                    viewModel.setSelectedLetter(firstLetter)
                                },
                                modifier = Modifier.weight(1f).testTag("select_letter_primary")
                            )

                            // O chip button
                            SophisticatedLetterButton(
                                letterSymbol = secondLetter,
                                isSelected = uiState.selectedLetter == secondLetter,
                                accentColor = animatedTurnColor,
                                p1Color = p1Color,
                                p2Color = p2Color,
                                onClick = {
                                    SoundEffects.playButtonSound()
                                    viewModel.setSelectedLetter(secondLetter)
                                },
                                modifier = Modifier.weight(1f).testTag("select_letter_secondary")
                            )
                        }
                    }
                }

                // TOOLBAR ACTION BUTTONS (Reset and Next Round)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = { viewModel.startNextRound() },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("new_round_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = p1Color),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = null, tint = Color(0xFF381E72), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("NEW MATCH", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF381E72), letterSpacing = 1.sp)
                    }

                    Button(
                        onClick = { viewModel.resetMatchScores() },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("reset_scores_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        border = androidx.compose.foundation.BorderStroke(1.dp, sophBorderColor),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = null, tint = p1Color, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("RESET SCORES", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = p1Color, letterSpacing = 1.sp)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    // ROUND OVER OVERLAY DIALOG (For multi-round Best of N progression or Casual round restarts)
    if (uiState.showRoundOverDialog) {
        val dialogAccentColor = when (uiState.roundWinnerId) {
            1 -> p1Color
            2 -> p2Color
            else -> Color.White
        }
        AlertDialog(
            onDismissRequest = {},
            confirmButton = {
                Button(
                    onClick = { viewModel.startNextRound() },
                    colors = ButtonDefaults.buttonColors(containerColor = p1Color),
                    modifier = Modifier.testTag("dialog_next_round_btn")
                ) {
                    Text("Play Next Round", fontWeight = FontWeight.Bold, color = Color(0xFF381E72))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        viewModel.resetMatchScores()
                        viewModel.navigateTo(AppScreen.SETUP)
                    },
                    modifier = Modifier.testTag("dialog_exit_setup_btn")
                ) {
                    Text("Leave Match", color = Color(0xFF938F99))
                }
            },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Star, contentDescription = null, tint = dialogAccentColor, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("ROUND OVER!", fontWeight = FontWeight.Bold, color = Color.White)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    val winnerMessage = when (uiState.roundWinnerId) {
                        1 -> "${uiState.playerOneName} wins the round!"
                        2 -> "${uiState.playerTwoName} wins the round!"
                        else -> "The round ended in a DRAW!"
                    }

                    Text(
                        text = winnerMessage,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = dialogAccentColor
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    Text("ROUND SCORES SUMMARY:", fontSize = 12.sp, color = Color(0xFF938F99), fontWeight = FontWeight.SemiBold)
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("${uiState.playerOneName}:", color = Color.White)
                        Text("${uiState.playerOneRoundScore} points", fontWeight = FontWeight.Bold, color = p1Color)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("${uiState.playerTwoName}:", color = Color.White)
                        Text("${uiState.playerTwoRoundScore} points", fontWeight = FontWeight.Bold, color = p2Color)
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text("MATCH WINS TRACK:", fontSize = 12.sp, color = Color(0xFF938F99), fontWeight = FontWeight.SemiBold)
                    Text(
                        text = "${uiState.playerOneName} [${uiState.playerOneMatchWins}] - [${uiState.playerTwoMatchWins}] ${uiState.playerTwoName}",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            },
            containerColor = sophCardBg,
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.testTag("round_over_dialog")
        )
    }

    // ULTIMATE MATCH OVER DIALOG (Best of N champion confirmed)
    if (uiState.showMatchOverDialog) {
        val ultimateWinnerName = if (uiState.matchWinnerId == 1) uiState.playerOneName else uiState.playerTwoName
        val ultimateWinnerColor = if (uiState.matchWinnerId == 1) p1Color else p2Color

        AlertDialog(
            onDismissRequest = {},
            confirmButton = {
                Button(
                    onClick = { viewModel.restartMatch() },
                    colors = ButtonDefaults.buttonColors(containerColor = ultimateWinnerColor),
                    modifier = Modifier.testTag("dialog_play_again_btn")
                ) {
                    Text("Rematch", fontWeight = FontWeight.Bold, color = Color(0xFF381E72))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        viewModel.resetMatchScores()
                        viewModel.navigateTo(AppScreen.SETUP)
                    },
                    modifier = Modifier.testTag("dialog_main_menu_btn")
                ) {
                    Text("Main Menu", color = Color(0xFF938F99))
                }
            },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = ultimateWinnerColor, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("CHAMPION DECREED! 🏆", fontWeight = FontWeight.Bold, color = Color.White)
                }
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)
                ) {
                    Text(
                        text = "CHAMPION",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF938F99),
                        letterSpacing = 2.sp
                    )
                    
                    Text(
                        text = ultimateWinnerName.uppercase(),
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black,
                        color = ultimateWinnerColor,
                        fontFamily = FontFamily.Serif,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Match series completed successfully.",
                        fontSize = 12.sp,
                        color = Color.LightGray
                    )
                    Text(
                        text = "Final Series: ${uiState.playerOneName} (${uiState.playerOneMatchWins}) vs ${uiState.playerTwoName} (${uiState.playerTwoMatchWins})",
                        fontSize = 12.sp,
                        color = Color(0xFF938F99),
                        textAlign = TextAlign.Center
                    )
                }
            },
            containerColor = sophCardBg,
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.testTag("match_over_dialog")
        )
    }
}

val GameViewModel.p1MatchWinsCount: Int
    @Composable get() = uiState.collectAsState().value.playerOneMatchWins

val GameViewModel.p2MatchWinsCount: Int
    @Composable get() = uiState.collectAsState().value.playerTwoMatchWins

@Composable
fun SophisticatedLetterButton(
    letterSymbol: String,
    isSelected: Boolean,
    accentColor: Color,
    p1Color: Color,
    p2Color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scale by animateFloatAsState(if (isSelected) 1.05f else 1.0f)
    
    Box(
        modifier = modifier
            .scale(scale)
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) p1Color else Color(0xFF49454F),
                shape = RoundedCornerShape(16.dp)
            )
            .background(
                color = if (isSelected) p1Color else Color(0xFF4A4458),
                shape = RoundedCornerShape(16.dp)
            )
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = letterSymbol,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = if (isSelected) Color(0xFF381E72) else Color(0xFFE8DEF8)
        )
    }
}

@Composable
fun BoardCellView(
    cellState: com.example.model.BoardCell,
    partOfP1Match: Boolean,
    partOfP2Match: Boolean,
    p1Color: Color,
    p2Color: Color,
    sophCardBg: Color,
    sophBorderColor: Color,
    sophActiveCellBg: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scaleEffect by animateFloatAsState(
        targetValue = if (cellState.letter != null) 1f else 0.8f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 400f)
    )

    // Highlight cell when it's part of a winning pattern
    val isHighlighted = partOfP1Match || partOfP2Match

    val backgroundBrush = when {
        partOfP1Match -> Brush.radialGradient(listOf(p1Color.copy(alpha = 0.35f), Color.Transparent))
        partOfP2Match -> Brush.radialGradient(listOf(p2Color.copy(alpha = 0.35f), Color.Transparent))
        else -> Brush.radialGradient(listOf(sophCardBg, Color(0xFF1C1B1F)))
    }

    val finalBackground = if (isHighlighted) sophActiveCellBg else Color.Transparent

    val cellBorderColor = when {
        partOfP1Match -> p1Color
        partOfP2Match -> p2Color
        cellState.letter != null -> sophBorderColor
        else -> sophBorderColor.copy(alpha = 0.6f)
    }

    val contentColor = when (cellState.letter) {
        "S", "X" -> p1Color
        "O" -> p2Color
        else -> Color.White
    }

    Box(
        modifier = modifier
            .shadow(
                elevation = if (isHighlighted) 12.dp else 0.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = if (partOfP1Match) p1Color else p2Color,
                spotColor = if (partOfP1Match) p1Color else p2Color
            )
            .background(backgroundBrush, RoundedCornerShape(16.dp))
            .background(finalBackground, RoundedCornerShape(16.dp))
            .border(
                width = if (isHighlighted) 2.5.dp else 1.dp,
                color = cellBorderColor,
                shape = RoundedCornerShape(16.dp)
            )
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(
            visible = cellState.letter != null,
            enter = fadeIn(animationSpec = tween(150)) + scaleIn(initialScale = 0.4f, animationSpec = spring(dampingRatio = 0.55f))
        ) {
            Text(
                text = cellState.letter ?: "",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = contentColor,
                fontFamily = FontFamily.Serif,
                modifier = Modifier.scale(scaleEffect)
            )
        }
    }
}
