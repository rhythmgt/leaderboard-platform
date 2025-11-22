package com.platforms.leaderboard.configservice.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import java.time.Duration

/**
 * Configuration properties for the leaderboard service
 */
@ConfigurationProperties(prefix = "leaderboard.config")
data class LeaderboardConfigProperties(
    val defaults: Defaults = Defaults(),
    val cache: Cache = Cache()
) {
    data class Defaults(
        /**
         * Default ranking order if not specified
         */
        val rankingOrder: String = "HIGHEST_FIRST",
        
        /**
         * Default leaderboard status when created
         */
        val status: String = "DRAFT",
        
        /**
         * Default number of partitions for new topics
         */
        val topicPartitions: Int = 3,
        
        /**
         * Default replication factor for new topics
         */
        val topicReplicationFactor: Short = 1
    )
    
    data class Cache(
        /**
         * Cache TTL for leaderboard configurations
         */
        val ttl: Duration = Duration.ofMinutes(30),
        
        /**
         * Maximum number of entries in the cache
         */
        val maxSize: Int = 1000
    )
}
