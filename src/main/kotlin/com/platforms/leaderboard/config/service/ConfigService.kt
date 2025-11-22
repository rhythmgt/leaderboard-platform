package com.platforms.leaderboard.config.service

/**
 * Represents the configuration for a leaderboard instance
 * @property highestFirst Whether the leaderboard should be sorted with highest scores first
 */
data class LeaderboardConfig(
    val highestFirst: Boolean = true
)

interface ConfigService {
    /**
     * Get the complete configuration for a leaderboard instance
     * @param instanceId The ID of the leaderboard instance
     * @return The complete configuration for the instance
     */
    suspend fun getLeaderboardConfig(instanceId: String): LeaderboardConfig
}
