package com.example.viewmodel

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.GameRepository
import com.example.data.MatchHistoryEntity
import com.example.model.BoardCell
import com.example.model.CompletedPattern
import com.example.model.GameMode
import com.example.model.MatchLimit
import com.example.model.MatchStats
import com.example.util.SoundEffects
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class AppScreen {
    SETUP, GAME, STATS
}

data class GameUiState(
    val currentScreen: AppScreen = AppScreen.SETUP,
    val playerOneName: String = "Player 1",
    val playerTwoName: String = "Player 2",
    val gameMode: GameMode = GameMode.SOS,
    val matchLimit: MatchLimit = MatchLimit.BEST_OF_3,
    
    // Board state (9 cells)
    val board: List<BoardCell> = List(9) { BoardCell(it) },
    val activePlayerId: Int = 1, // 1 or 2
    val selectedLetter: String = "S", // "S"/"O" or "X"/"O"
    
    // Current round scores
    val playerOneRoundScore: Int = 0,
    val playerTwoRoundScore: Int = 0,
    
    // Total rounds won in this match
    val playerOneMatchWins: Int = 0,
    val playerTwoMatchWins: Int = 0,
    
    // Pattern highlight trackers
    val completedPatterns: List<CompletedPattern> = emptyList(),
    
    // Dialog overlays
    val showRoundOverDialog: Boolean = false,
    val showMatchOverDialog: Boolean = false,
    val roundWinnerId: Int? = null, // 1, 2, or 0 (Draw)
    val matchWinnerId: Int? = null, // 1 or 2
    
    // Track count of SOS / XOX patterns created during the current match to record in history
    val currentMatchSosCount: Int = 0,
    val currentMatchXoxCount: Int = 0
)

class GameViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository: GameRepository
    
    init {
        val database = AppDatabase.getInstance(application)
        repository = GameRepository(database.matchHistoryDao)
    }

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    // Query match history from DB to build statistics and historical lists reactively
    val matchHistory: StateFlow<List<MatchHistoryEntity>> = repository.allMatches
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Compute stats from match history
    val stats: StateFlow<MatchStats> = matchHistory.map { history ->
        var sosWins = 0
        var xoxWins = 0
        var playerOneWins = 0
        var playerTwoWins = 0
        var draws = 0
        var totalSos = 0
        var totalXox = 0
        
        history.forEach { record ->
            totalSos += record.totalSosFormed
            totalXox += record.totalXoxFormed
            
            if (record.gameMode == "SOS") {
                if (record.winnerName != "Draw") {
                    sosWins++
                }
            } else {
                if (record.winnerName != "Draw") {
                    xoxWins++
                }
            }

            when {
                record.winnerName == "Draw" -> draws++
                record.winnerName == record.playerOneName -> playerOneWins++
                record.winnerName == record.playerTwoName -> playerTwoWins++
            }
        }

        MatchStats(
            totalMatches = history.size,
            sosWins = sosWins,
            xoxWins = xoxWins,
            playerOneWins = playerOneWins,
            playerTwoWins = playerTwoWins,
            draws = draws,
            totalSOSFormed = totalSos,
            totalXOXFormed = totalXox
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = MatchStats()
    )

    // Setup input configuration handlers
    fun setPlayerOneName(name: String) {
        _uiState.update { it.copy(playerOneName = name.ifBlank { "Player 1" }) }
    }

    fun setPlayerTwoName(name: String) {
        _uiState.update { it.copy(playerTwoName = name.ifBlank { "Player 2" }) }
    }

    fun setGameMode(mode: GameMode) {
        _uiState.update { state ->
            val letter = if (mode == GameMode.SOS) "S" else "X"
            state.copy(gameMode = mode, selectedLetter = letter)
        }
    }

    fun setMatchLimit(limit: MatchLimit) {
        _uiState.update { it.copy(matchLimit = limit) }
    }

    fun setSelectedLetter(letter: String) {
        _uiState.update { it.copy(selectedLetter = letter) }
    }

    fun navigateTo(screen: AppScreen) {
        _uiState.update { it.copy(currentScreen = screen) }
    }

    // Start playing with current parameters
    fun startNewMatch() {
        _uiState.update { state ->
            state.copy(
                currentScreen = AppScreen.GAME,
                board = List(9) { BoardCell(it) },
                activePlayerId = 1,
                selectedLetter = if (state.gameMode == GameMode.SOS) "S" else "X",
                playerOneRoundScore = 0,
                playerTwoRoundScore = 0,
                playerOneMatchWins = 0,
                playerTwoMatchWins = 0,
                completedPatterns = emptyList(),
                showRoundOverDialog = false,
                showMatchOverDialog = false,
                roundWinnerId = null,
                matchWinnerId = null,
                currentMatchSosCount = 0,
                currentMatchXoxCount = 0
            )
        }
    }

    // Restart the current match (keeps setup names/mode, resets scores and wins to 0)
    fun restartMatch() {
        startNewMatch()
        SoundEffects.playButtonSound()
    }

    // Setup a new round (advances score tracker, clears board, retains match wins)
    fun startNextRound() {
        _uiState.update { state ->
            state.copy(
                board = List(9) { BoardCell(it) },
                activePlayerId = 1,
                selectedLetter = if (state.gameMode == GameMode.SOS) "S" else "X",
                playerOneRoundScore = 0,
                playerTwoRoundScore = 0,
                completedPatterns = emptyList(),
                showRoundOverDialog = false,
                roundWinnerId = null
            )
        }
        SoundEffects.playButtonSound()
    }

    // Reset accumulated local scores back to zero (clears match-level wins)
    fun resetMatchScores() {
        _uiState.update { state ->
            state.copy(
                playerOneMatchWins = 0,
                playerTwoMatchWins = 0,
                playerOneRoundScore = 0,
                playerTwoRoundScore = 0,
                board = List(9) { BoardCell(it) },
                completedPatterns = emptyList(),
                activePlayerId = 1,
                selectedLetter = if (state.gameMode == GameMode.SOS) "S" else "X",
                showRoundOverDialog = false,
                showMatchOverDialog = false,
                roundWinnerId = null,
                matchWinnerId = null,
                currentMatchSosCount = 0,
                currentMatchXoxCount = 0
            )
        }
        SoundEffects.playButtonSound()
    }

    // Clear whole DB Match History
    fun clearDatabaseHistory() {
        viewModelScope.launch {
            repository.clearHistory()
        }
        SoundEffects.playButtonSound()
    }

    // Main Gameplay Click Handler (Cell Placement)
    fun onCellClicked(cellIndex: Int, onHapticFeedback: () -> Unit) {
        val currentState = _uiState.value
        
        // Block interaction if cell is already placed, dialog is showing or match over
        if (currentState.board[cellIndex].letter != null || 
            currentState.showRoundOverDialog || 
            currentState.showMatchOverDialog) {
            return
        }

        // Apply Haptic Feedback
        onHapticFeedback()

        // Place Letter
        val placedLetter = currentState.selectedLetter
        val updatedBoard = currentState.board.map { cell ->
            if (cell.index == cellIndex) cell.copy(letter = placedLetter) else cell
        }

        // List of all 8 potential patterns of length 3 on a 3x3 board
        val linePaths = listOf(
            listOf(0, 1, 2), // Horizontal 0
            listOf(3, 4, 5), // Horizontal 1
            listOf(6, 7, 8), // Horizontal 2
            listOf(0, 3, 6), // Vertical 0
            listOf(1, 4, 7), // Vertical 1
            listOf(2, 5, 8), // Vertical 2
            listOf(0, 4, 8), // Diagonal 1
            listOf(2, 4, 6)  // Diagonal 2
        )

        val completedType = if (currentState.gameMode == GameMode.SOS) "SOS" else "XOX"
        val activePlayer = currentState.activePlayerId
        
        val newlyCompletedPatterns = mutableListOf<CompletedPattern>()

        // Analyze which lines represent a pattern, ignoring any pattern coordinates already completed
        for (indexes in linePaths) {
            val cellA = updatedBoard[indexes[0]]
            val cellB = updatedBoard[indexes[1]]
            val cellC = updatedBoard[indexes[2]]

            val isPatternMatch = if (currentState.gameMode == GameMode.SOS) {
                cellA.letter == "S" && cellB.letter == "O" && cellC.letter == "S"
            } else {
                cellA.letter == "X" && cellB.letter == "O" && cellC.letter == "X"
            }

            if (isPatternMatch) {
                val patternId = "${indexes[0]}-${indexes[1]}-${indexes[2]}"
                val alreadyFormed = currentState.completedPatterns.any { it.id == patternId }
                
                // Real match check: must contain the cell we just clicked to represent "forms a pattern on your turn"
                val containsMove = indexes.contains(cellIndex)

                if (!alreadyFormed && containsMove) {
                    newlyCompletedPatterns.add(
                        CompletedPattern(
                            id = patternId,
                            indexes = indexes,
                            playerOwnerId = activePlayer,
                            type = completedType
                        )
                    )
                }
            }
        }

        val patternMatchesCount = newlyCompletedPatterns.size
        
        // Calculate updated score & letter counts
        var newP1Score = currentState.playerOneRoundScore
        var newP2Score = currentState.playerTwoRoundScore
        var matchSosAddition = currentState.currentMatchSosCount
        var matchXoxAddition = currentState.currentMatchXoxCount

        if (currentState.gameMode == GameMode.SOS) {
            matchSosAddition += patternMatchesCount
        } else {
            matchXoxAddition += patternMatchesCount
        }

        if (patternMatchesCount > 0) {
            if (activePlayer == 1) {
                newP1Score += patternMatchesCount
            } else {
                newP2Score += patternMatchesCount
            }
        }

        // Determine next active player.
        // Rule: Player who creates a pattern gets another turn!
        val nextPlayerId = if (patternMatchesCount > 0) {
            activePlayer // retain turn
        } else {
            if (activePlayer == 1) 2 else 1 // alternate turn
        }

        // Set default placement letter for the active player's turn to ensure smoothness
        val nextDefaultLetter = if (currentState.gameMode == GameMode.SOS) "S" else "X"

        val finalPatterns = currentState.completedPatterns + newlyCompletedPatterns
        val finalBoardFull = updatedBoard.all { it.letter != null }

        _uiState.update { state ->
            state.copy(
                board = updatedBoard,
                completedPatterns = finalPatterns,
                playerOneRoundScore = newP1Score,
                playerTwoRoundScore = newP2Score,
                currentMatchSosCount = matchSosAddition,
                currentMatchXoxCount = matchXoxAddition,
                activePlayerId = nextPlayerId,
                selectedLetter = nextDefaultLetter
            )
        }

        if (patternMatchesCount > 0) {
            SoundEffects.playWinSound()
        } else {
            SoundEffects.playMoveSound()
        }

        // Check if board is complete (full)
        if (finalBoardFull) {
            processRoundEnd()
        }
    }

    // Process conclusion of a round (full board)
    private fun processRoundEnd() {
        val state = _uiState.value
        val p1RoundScore = state.playerOneRoundScore
        val p2RoundScore = state.playerTwoRoundScore
        
        val roundWinner = when {
            p1RoundScore > p2RoundScore -> 1
            p2RoundScore > p1RoundScore -> 2
            else -> 0 // Draw
        }

        var newP1Wins = state.playerOneMatchWins
        var newP2Wins = state.playerTwoMatchWins

        if (roundWinner == 1) {
            newP1Wins++
        } else if (roundWinner == 2) {
            newP2Wins++
        }

        _uiState.update { it.copy(playerOneMatchWins = newP1Wins, playerTwoMatchWins = newP2Wins) }

        // Determine if match target winner criteria met
        val reqWins = state.matchLimit.requiredWins
        val isMatchOver = when (state.matchLimit) {
            MatchLimit.CASUAL -> false // never ending mode unless restarted
            else -> newP1Wins >= reqWins || newP2Wins >= reqWins
        }

        if (isMatchOver) {
            val ultimateWinnerId = if (newP1Wins >= reqWins) 1 else 2
            val winnerNameText = if (ultimateWinnerId == 1) state.playerOneName else state.playerTwoName
            
            _uiState.update {
                it.copy(
                    showMatchOverDialog = true,
                    matchWinnerId = ultimateWinnerId
                )
            }
            SoundEffects.playWinSound()
            
            // Save final match stats to Room records
            viewModelScope.launch {
                val record = MatchHistoryEntity(
                    playerOneName = state.playerOneName,
                    playerTwoName = state.playerTwoName,
                    playerOneRoundsWon = newP1Wins,
                    playerTwoRoundsWon = newP2Wins,
                    gameMode = state.gameMode.name,
                    matchLimit = state.matchLimit.displayName,
                    winnerName = winnerNameText,
                    totalSosFormed = state.currentMatchSosCount,
                    totalXoxFormed = state.currentMatchXoxCount
                )
                repository.insertMatch(record)
            }
        } else {
            // Casual game round ends when board is full
            _uiState.update {
                it.copy(
                    showRoundOverDialog = true,
                    roundWinnerId = roundWinner
                )
            }
            if (roundWinner == 0) {
                SoundEffects.playDrawSound()
            } else {
                SoundEffects.playWinSound()
            }

            // If game is Casual, we still write each round/game as a match history row
            if (state.matchLimit == MatchLimit.CASUAL) {
                viewModelScope.launch {
                    val winnerNameText = when (roundWinner) {
                        1 -> state.playerOneName
                        2 -> state.playerTwoName
                        else -> "Draw"
                    }
                    val record = MatchHistoryEntity(
                        playerOneName = state.playerOneName,
                        playerTwoName = state.playerTwoName,
                        playerOneRoundsWon = if (roundWinner == 1) 1 else 0,
                        playerTwoRoundsWon = if (roundWinner == 2) 1 else 0,
                        gameMode = state.gameMode.name,
                        matchLimit = state.matchLimit.displayName,
                        winnerName = winnerNameText,
                        totalSosFormed = state.currentMatchSosCount,
                        totalXoxFormed = state.currentMatchXoxCount
                    )
                    repository.insertMatch(record)
                }
            }
        }
    }
}
