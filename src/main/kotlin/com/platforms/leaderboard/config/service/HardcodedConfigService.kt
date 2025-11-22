package com.platforms.leaderboard.config.service

import org.springframework.stereotype.Service

/**
 * A simple implementation of ConfigService that returns hardcoded values.
 * In a real application, this would fetch configuration from a configuration service.
 */
@Service
class HardcodedConfigService : ConfigService {
    
    private val configs = mapOf(
        // Default configuration for all instances
        "*" to LeaderboardConfig(
            highestFirst = true
        ),
        // You can override the default for specific instance IDs like this:
        // "instance123" to LeaderboardConfig(
        //     highestFirst = false
        // )
    )

    override suspend fun getLeaderboardConfig(instanceId: String): LeaderboardConfig {
        return configs[instanceId] ?: configs["*"]!!
    }
}
