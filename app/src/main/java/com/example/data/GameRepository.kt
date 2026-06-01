package com.example.data

import kotlinx.coroutines.flow.Flow

class GameRepository(private val dao: MatchHistoryDao) {
    val allMatches: Flow<List<MatchHistoryEntity>> = dao.getAllMatches()

    suspend fun insertMatch(match: MatchHistoryEntity) {
        dao.insertMatch(match)
    }

    suspend fun clearHistory() {
        dao.clearHistory()
    }
}
