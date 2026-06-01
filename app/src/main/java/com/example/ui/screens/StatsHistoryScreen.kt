package com.example.ui.screens

import android.text.format.DateFormat
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Refresh
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.MatchHistoryEntity
import com.example.util.SoundEffects
import com.example.viewmodel.AppScreen
import com.example.viewmodel.GameViewModel
import java.util.Date

@Composable
fun StatsHistoryScreen(
    viewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    val stats by viewModel.stats.collectAsState()
    val history by viewModel.matchHistory.collectAsState()
    val scrollState = rememberScrollState()

    val sophBackground = Color(0xFF1C1B1F)
    val sophCardBg = Color(0xFF2B2930)
    val sophBorderColor = Color(0xFF49454F)
    
    val p1Color = Color(0xFFD0BCFF) // Soft Lavender (Alex color vibe)
    val p2Color = Color(0xFFCCC2DC) // Muted Lavender-Gray (Jordan color vibe)

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
            // Header with Back Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                    IconButton(
                    onClick = {
                        SoundEffects.playButtonSound()
                        viewModel.navigateTo(AppScreen.SETUP)
                    },
                    modifier = Modifier
                        .background(sophCardBg, RoundedCornerShape(12.dp))
                        .border(1.dp, sophBorderColor, RoundedCornerShape(12.dp))
                        .testTag("back_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = p1Color
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "DASHBOARD",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Serif,
                    color = Color.White
                )
            }

            // Stat Cards Grid
            Text(
                text = "GLOBAL STATISTICS",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = p1Color,
                letterSpacing = 1.5.sp,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatItemCard(
                    title = "Total Duels",
                    value = stats.totalMatches.toString(),
                    icon = Icons.Default.Star,
                    accentColor = p1Color,
                    sophCardBg = sophCardBg,
                    sophBorderColor = sophBorderColor,
                    modifier = Modifier.weight(1.0f).testTag("stat_total_duels")
                )
                StatItemCard(
                    title = "Draw Matches",
                    value = stats.draws.toString(),
                    icon = Icons.Default.Info,
                    accentColor = Color(0xFF938F99),
                    sophCardBg = sophCardBg,
                    sophBorderColor = sophBorderColor,
                    modifier = Modifier.weight(1.0f).testTag("stat_total_draws")
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatItemCard(
                    title = "SOS Created",
                    value = stats.totalSOSFormed.toString(),
                    icon = Icons.Default.Star,
                    accentColor = p1Color,
                    sophCardBg = sophCardBg,
                    sophBorderColor = sophBorderColor,
                    modifier = Modifier.weight(1.0f).testTag("stat_total_sos")
                )
                StatItemCard(
                    title = "XOX Created",
                    value = stats.totalXOXFormed.toString(),
                    icon = Icons.Default.Star,
                    accentColor = p2Color,
                    sophCardBg = sophCardBg,
                    sophBorderColor = sophBorderColor,
                    modifier = Modifier.weight(1.0f).testTag("stat_total_xox")
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // History Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.List,
                        contentDescription = null,
                        tint = p2Color,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "MATCH LOG HISTORY",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = p2Color,
                        letterSpacing = 1.5.sp
                    )
                }

                if (history.isNotEmpty()) {
                    TextButton(
                        onClick = { viewModel.clearDatabaseHistory() },
                        colors = ButtonDefaults.textButtonColors(contentColor = p2Color),
                        modifier = Modifier.testTag("clear_history_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Clear All", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            if (history.isEmpty()) {
                // Empty state card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = sophCardBg),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, sophBorderColor)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp).fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "No recorded duels yet.",
                            color = Color(0xFF938F99),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Play a complete round or match in GAME mode to see history records here!",
                            color = Color(0xFF938F99).copy(alpha = 0.7f),
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                history.forEach { record ->
                    HistoryItemRow(
                        record = record,
                        p1Color = p1Color,
                        p2Color = p2Color,
                        sophBorderColor = sophBorderColor,
                        containerBg = sophCardBg,
                        modifier = Modifier.testTag("history_item_${record.id}")
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun StatItemCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accentColor: Color,
    sophCardBg: Color,
    sophBorderColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = sophCardBg),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, sophBorderColor)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title.uppercase(),
                    fontSize = 10.sp,
                    color = Color(0xFF938F99),
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(16.dp)
                )
            }

            Text(
                text = value,
                fontSize = 28.sp,
                fontFamily = androidx.compose.ui.text.font.FontFamily.Serif,
                fontWeight = FontWeight.Normal,
                color = Color.White
            )
        }
    }
}

@Composable
fun HistoryItemRow(
    record: MatchHistoryEntity,
    p1Color: Color,
    p2Color: Color,
    sophBorderColor: Color,
    containerBg: Color,
    modifier: Modifier = Modifier
) {
    val dateText = DateFormat.format("MMM dd, yyyy · hh:mm a", Date(record.timestamp)).toString()
    
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = containerBg),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, sophBorderColor)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Header line (date and game mode tag)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = dateText,
                    fontSize = 11.sp,
                    color = Color(0xFF938F99),
                    fontWeight = FontWeight.Normal
                )

                // Mode badge
                Box(
                    modifier = Modifier
                        .background(
                            color = if (record.gameMode == "SOS") p1Color.copy(alpha = 0.15f) else p2Color.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(6.dp)
                        )
                        .border(1.dp, if (record.gameMode == "SOS") p1Color.copy(alpha = 0.3f) else p2Color.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "${record.gameMode} (${record.matchLimit})",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (record.gameMode == "SOS") p1Color else p2Color
                    )
                }
            }

            // Central scores columns
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Player 1 Details
                Column(
                    modifier = Modifier.weight(1.0f),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = record.playerOneName,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        maxLines = 1
                    )
                    Text(
                        text = "Rounds won: ${record.playerOneRoundsWon}",
                        fontSize = 12.sp,
                        color = Color(0xFFE6E1E5).copy(alpha = 0.7f)
                    )
                }

                // Score separator
                Text(
                    text = "VS",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF49454F),
                    modifier = Modifier.padding(horizontal = 4.dp)
                )

                // Player 2 Details
                Column(
                    modifier = Modifier.weight(1.0f),
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = record.playerTwoName,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.End,
                        maxLines = 1
                    )
                    Text(
                        text = "Rounds won: ${record.playerTwoRoundsWon}",
                        fontSize = 12.sp,
                        color = Color(0xFFE6E1E5).copy(alpha = 0.7f),
                        textAlign = TextAlign.End
                    )
                }
            }

            // Footer (Winner declaration or Draw)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF1C1B1F), RoundedCornerShape(8.dp))
                    .border(1.dp, sophBorderColor, RoundedCornerShape(8.dp))
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                val isDraw = record.winnerName == "Draw"
                val winnerColor = when {
                    isDraw -> Color(0xFF938F99)
                    record.winnerName == record.playerOneName -> p1Color
                    else -> p2Color
                }
                
                Text(
                    text = if (isDraw) "RESULT: Match Drawn 🤝" else "VICTOR: ${record.winnerName} 👑",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = winnerColor
                )
            }
        }
    }
}
