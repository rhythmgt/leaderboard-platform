package com.platforms.leaderboard.read.repository

import com.platforms.leaderboard.read.domain.LeaderboardEntry
import org.slf4j.LoggerFactory
import org.springframework.dao.DataAccessException
import org.springframework.stereotype.Component

/**
 * A composite repository that first tries Redis and falls back to SQL if Redis is unavailable.
 */
@Component
class CompositeLeaderboardReadRepository(
    private val redisRepository: RedisLeaderboardReadRepository,
    private val sqlRepository: SqlLeaderboardReadRepository
) : LeaderboardReadRepository {

    private val logger = LoggerFactory.getLogger(javaClass)

    override suspend fun getTopK(
        instanceId: String,
        limit: Int,
        offset: Int,
        isHighestFirst: Boolean
    ): List<LeaderboardEntry> {
        return try {
            redisRepository.getTopK(instanceId, limit, offset, isHighestFirst)
        } catch (e: DataAccessException) {
            logger.warn("Redis access failed, falling back to SQL", e)
            sqlRepository.getTopK(instanceId, limit, offset, isHighestFirst)
        }
    }

    override suspend fun getUserRank(
        instanceId: String,
        userId: String,
        isHighestFirst: Boolean
    ): LeaderboardEntry? {
        return try {
            redisRepository.getUserRank(instanceId, userId, isHighestFirst)
                ?: sqlRepository.getUserRank(instanceId, userId, isHighestFirst)
        } catch (e: DataAccessException) {
            logger.warn("Redis access failed, falling back to SQL", e)
            sqlRepository.getUserRank(instanceId, userId, isHighestFirst)
        }
    }

    override suspend fun getAroundUser(
        instanceId: String,
        userId: String,
        limit: Int,
        isHighestFirst: Boolean
    ): List<LeaderboardEntry> {
        return try {
            val redisResult = redisRepository.getAroundUser(instanceId, userId, limit, isHighestFirst)
            if (redisResult.isNotEmpty()) {
                redisResult
            } else {
                sqlRepository.getAroundUser(instanceId, userId, limit, isHighestFirst)
            }
        } catch (e: DataAccessException) {
            logger.warn("Redis access failed, falling back to SQL", e)
            sqlRepository.getAroundUser(instanceId, userId, limit, isHighestFirst)
        }
    }
}
