package com.platforms.leaderboard.read.controller

import com.platforms.leaderboard.read.domain.LeaderboardEntry
import com.platforms.leaderboard.read.service.LeaderboardService
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1")
@Validated
class UserScoreReadController(
    private val leaderboardService: LeaderboardService
) {
    companion object {
        private const val DEFAULT_TOP_LIMIT = 10
        private const val MAX_TOP_LIMIT = 100
    }

    /**
     * Get top users from the leaderboard
     * @param instanceId The ID of the leaderboard instance
     * @param limit Maximum number of results to return (1-100, default: 10)
     * @return List of leaderboard entries with 1-based ranks
     * @throws javax.validation.ConstraintViolationException if limit is out of range
     */
    @GetMapping("/leaderboard/{leaderboardInstanceId}/user-score/top")
    suspend fun getTop(
        @PathVariable leaderboardInstanceId: String,
        @RequestParam(defaultValue = "10") limit: Int
    ): List<LeaderboardEntry> {
        return leaderboardService.getTop(leaderboardInstanceId, limit)
    }

    /**
     * Get a user's rank and score from the leaderboard
     * @param userId The ID of the user
     * @param leaderboardInstanceId The ID of the leaderboard instance
     * @return The leaderboard entry for the user with 1-based rank, or null if not found
     */
    @GetMapping("/leaderboard/{leaderboardInstanceId}/user-score/{userId}")
    suspend fun getUserRank(
        @PathVariable userId: String,
        @RequestParam leaderboardInstanceId: String
    ): LeaderboardEntry? {
        return leaderboardService.getUserRank(userId, leaderboardInstanceId)
    }
}
