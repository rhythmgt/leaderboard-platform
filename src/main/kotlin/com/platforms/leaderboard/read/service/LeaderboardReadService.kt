package com.platforms.leaderboard.read.service

import com.platforms.leaderboard.read.domain.LeaderboardEntry
import com.platforms.leaderboard.read.repository.CompositeLeaderboardReadRepository
import org.springframework.stereotype.Service

@Service
class LeaderboardReadService(
    private val leaderboardReadRepository: CompositeLeaderboardReadRepository
) {
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
        offset: Int = 0,
        isHighestFirst: Boolean = true
    ): List<LeaderboardEntry> {
        require(limit > 0) { "Limit must be greater than 0" }
        require(offset >= 0) { "Offset must be non-negative" }
        
        return leaderboardReadRepository.getTopK(instanceId, limit, isHighestFirst)
    }

    /**
     * Get the rank of a specific user in a leaderboard instance
     * @param instanceId The leaderboard instance ID
     * @param userId The user ID to get rank for
     * @param isHighestFirst Whether to rank by highest score first
     * @return The leaderboard entry with rank, or null if not found
     */
    suspend fun getUserRank(
        instanceId: String,
        userId: String,
        isHighestFirst: Boolean = true
    ): LeaderboardEntry? {
        return leaderboardReadRepository.getUserRank(instanceId, userId, isHighestFirst)
    }


}
