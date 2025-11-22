package com.platforms.leaderboard.write.controller

import com.platforms.leaderboard.write.dto.FeatureBasedScoreUpdateRequest
import com.platforms.leaderboard.write.dto.ScoreUpdateResponse
import com.platforms.leaderboard.write.service.LeaderboardWriteService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.slf4j.LoggerFactory

@RestController
@RequestMapping("/api/v1")
class UserScoreWriteController(
    private val leaderboardWriteService: LeaderboardWriteService
) {
    private val logger = LoggerFactory.getLogger(javaClass)
    
    /**
     * Update score with features that will be used to calculate the score
     */
    @PostMapping("/user-score")
    suspend fun updateScoreWithFeatures(
        @RequestBody request: FeatureBasedScoreUpdateRequest
    ): ResponseEntity<ScoreUpdateResponse> {
        return try {
            val score = leaderboardWriteService.updateScoreWithFeatures(
                leaderboardInstanceId = request.leaderboardInstanceId,
                userId = request.userId,
                features = request.features
            )
            
            ResponseEntity.ok(ScoreUpdateResponse(
                success = true,
                message = "Score updated successfully",
                score = score
            ))
        } catch (e: Exception) {
            logger.error("Failed to update score with features for user ${request.userId} in leaderboard ${request.leaderboardInstanceId}", e)
            ResponseEntity.badRequest().body(ScoreUpdateResponse(
                success = false,
                message = "Failed to update score: ${e.message ?: "Unknown error"}",
                score = null
            ))
        }
    }
}
