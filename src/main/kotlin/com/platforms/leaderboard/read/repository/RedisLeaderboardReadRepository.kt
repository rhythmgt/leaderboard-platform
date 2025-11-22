package com.platforms.leaderboard.read.repository

import com.platforms.leaderboard.read.domain.LeaderboardEntry
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.ZSetOperations
import org.springframework.stereotype.Repository
import kotlin.getValue

@Repository
class RedisLeaderboardReadRepository(
    private val redisTemplate: RedisTemplate<String, String>
) : LeaderboardReadRepository {
    
    companion object {
        private const val LEADERBOARD_KEY_PREFIX = "leaderboard:"
    }

    private val zSetOps: ZSetOperations<String, String> by lazy {
        redisTemplate.opsForZSet()
    }

    override suspend fun getTopK(
        instanceId: String,
        limit: Int,
        isHighestFirst: Boolean
    ): List<LeaderboardEntry> {
        val key = getLeaderboardKey(instanceId)
        val range = if (isHighestFirst) {
            zSetOps.reverseRangeWithScores(key, 0, (limit - 1).toLong())
        } else {
            zSetOps.rangeWithScores(key, 0, (limit - 1).toLong())
        } ?: return emptyList()

        return range.mapIndexed { index, tuple ->
            val rank = index + 1L // 1-based ranking
            LeaderboardEntry(
                userId = tuple.value ?: "",
                score = tuple.score,
                rank = rank,
                additionalData = getUserData(instanceId, tuple.value ?: "")
            )
        }.toList()
    }

    override suspend fun getUserRank(
        instanceId: String,
        userId: String,
        isHighestFirst: Boolean
    ): LeaderboardEntry? {
        val key = getLeaderboardKey(instanceId)
        val score = zSetOps.score(key, userId) ?: return null
        
        val rank = if (isHighestFirst) {
            zSetOps.reverseRank(key, userId)?.plus(1) // Convert to 1-based
        } else {
            zSetOps.rank(key, userId)?.plus(1) // Convert to 1-based
        } ?: return null

        return LeaderboardEntry(
            userId = userId,
            score = score,
            rank = rank,
            additionalData = getUserData(instanceId, userId)
        )
    }



    private fun getLeaderboardKey(instanceId: String): String {
        return "$LEADERBOARD_KEY_PREFIX$instanceId"
    }

    private fun getUserData(instanceId: String, userId: String): Map<String, Any> {
        // In a real implementation, this would fetch additional user data from a separate hash
        // For now, return an empty map
        return emptyMap()
    }
}
