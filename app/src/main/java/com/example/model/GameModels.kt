package com.example.model

enum class GameMode {
    SOS, XOX
}

enum class MatchLimit(val displayName: String, val requiredWins: Int) {
    BEST_OF_1("Best of 1", 1),
    BEST_OF_3("Best of 3", 2),
    BEST_OF_5("Best of 5", 3),
    CASUAL("Casual (Infinite)", -1)
}

data class BoardCell(
    val index: Int,
    val letter: String? = null
)

data class CompletedPattern(
    val id: String, // e.g. "0-1-2"
    val indexes: List<Int>, // list of length 3
    val playerOwnerId: Int, // 1 or 2
    val type: String // "SOS" or "XOX"
)

data class MatchStats(
    val totalMatches: Int = 0,
    val sosWins: Int = 0,
    val xoxWins: Int = 0,
    val playerOneWins: Int = 0,
    val playerTwoWins: Int = 0,
    val draws: Int = 0,
    val totalSOSFormed: Int = 0,
    val totalXOXFormed: Int = 0
)
