package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "match_history")
data class MatchHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val playerOneName: String,
    val playerTwoName: String,
    val playerOneRoundsWon: Int,
    val playerTwoRoundsWon: Int,
    val gameMode: String, // "SOS" or "XOX"
    val matchLimit: String, // "Best of 1", "Best of 3", "Best of 5", "Casual"
    val winnerName: String, // "Player 1 Name", "Player 2 Name", or "Draw"
    val timestamp: Long = System.currentTimeMillis(),
    val totalSosFormed: Int = 0,
    val totalXoxFormed: Int = 0
)
