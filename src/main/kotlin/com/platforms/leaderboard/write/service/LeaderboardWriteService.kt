package com.platforms.leaderboard.write.service

import com.platforms.leaderboard.common.repository.CompositeLeaderboardRepository
import com.platforms.leaderboard.configservice.service.LeaderboardConfigService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class LeaderboardWriteService(
    private val userScoreWriteRepository: CompositeLeaderboardRepository,
    private val configService: LeaderboardConfigService,
    private val scoreCalculationService: ScoreCalculationService,
    private val eventValidationService: EventValidationService
) {
    private val logger = LoggerFactory.getLogger(javaClass)
    
    suspend fun updateScoreWithFeatures(
        leaderboardInstanceId: String,
        userId: String,
        features: Map<String, Any>
    ): Double {
        return withContext(Dispatchers.IO) {
            try {
                val config = configService.getConfig(leaderboardInstanceId)
                
                // Validate the incoming event against the config
                eventValidationService.validateEvent(features, config)
                
                // Calculate and save the score
                val score = scoreCalculationService.calculateScore(features, config.scoringStrategy)
                userScoreWriteRepository.saveScore(leaderboardInstanceId, userId, score)
                score
            } catch (e: Exception) {
                logger.error("Failed to update score with features for user $userId in leaderboard $leaderboardInstanceId", e)
                //todo: push the event to DLT here
                throw e
            }
        }
    }
}
