package com.platforms.leaderboard.common.repository

import com.platforms.leaderboard.read.domain.LeaderboardEntry

interface LeaderboardRepository {
    /**
     * Get top K entries from a leaderboard instance
     * @param instanceId The leaderboard instance ID
     * @param limit Maximum number of entries to return
     * @param offset Offset for pagination (0-based)
     * @param isHighestFirst Whether to sort by highest score first
     * @return List of leaderboard entries with ranks
     */
    suspend fun getTopK(
        instanceId: String,
        limit: Int,
        isHighestFirst: Boolean = true
    ): List<LeaderboardEntry>

    /**
     * Get the rank of a specific user in a leaderboard instance
     * @param instanceId The leaderboard instance ID
     * @param userId The user ID to get rank for
     * @param isHighestFirst Whether to rank by highest score first
     * @return The 1-based rank of the user, or null if not found
     */
    suspend fun getUserRank(
        instanceId: String,
        userId: String,
        isHighestFirst: Boolean = true
    ): LeaderboardEntry?

    suspend fun saveScore(leaderboardInstanceId: String, userId: String, score: Double)

}
