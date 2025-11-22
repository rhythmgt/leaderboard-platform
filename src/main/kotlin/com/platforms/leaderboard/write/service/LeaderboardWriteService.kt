package com.platforms.leaderboard.write.service

import com.platforms.leaderboard.common.repository.CompositeLeaderboardRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class LeaderboardWriteService(
    private val userScoreWriteRepository: CompositeLeaderboardRepository
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    suspend fun updateScore(
        leaderboardInstanceId: String,
        userId: String,
        score: Double
    ) {
        return withContext(Dispatchers.IO) {
            try {
                userScoreWriteRepository.saveScore(leaderboardInstanceId, userId, score)
            } catch (e: Exception) {
                logger.error("Failed to update score for user $userId in leaderboard $leaderboardInstanceId", e)
                false
            }
        }
    }
}
