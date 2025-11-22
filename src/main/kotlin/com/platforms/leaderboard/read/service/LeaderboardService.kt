package com.platforms.leaderboard.read.service

import com.platforms.leaderboard.read.domain.LeaderboardEntry

interface LeaderboardService {
    /**
     * Get top users from the leaderboard
     * @param instanceId The ID of the leaderboard instance
     * @param limit Maximum number of results to return (1-100, default: 10)
     * @return List of leaderboard entries with 1-based ranks
     */
    suspend fun getTop(instanceId: String, limit: Int): List<LeaderboardEntry>

    /**
     * Get a user's rank and score from the leaderboard
     * @param userId The ID of the user
     * @param instanceId The ID of the leaderboard instance
     * @return The leaderboard entry for the user with 1-based rank, or null if not found
     */
    suspend fun getUserRank(userId: String, instanceId: String): LeaderboardEntry?
}
