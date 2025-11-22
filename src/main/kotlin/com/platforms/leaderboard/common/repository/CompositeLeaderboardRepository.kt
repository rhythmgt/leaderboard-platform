package com.platforms.leaderboard.common.repository

import com.platforms.leaderboard.read.domain.LeaderboardEntry
import org.slf4j.LoggerFactory
import org.springframework.dao.DataAccessException
import org.springframework.stereotype.Component

/**
 * A composite repository that first tries Redis and falls back to SQL if Redis is unavailable.
 */
@Component
class CompositeLeaderboardRepository(
    private val redisRepository: RedisLeaderboardRepository,
    private val sqlRepository: SqlLeaderboardRepository
) : LeaderboardRepository {

    private val logger = LoggerFactory.getLogger(javaClass)

    override suspend fun getTopK(
        instanceId: String,
        limit: Int,
        isHighestFirst: Boolean
    ): List<LeaderboardEntry> {
        return try {
            redisRepository.getTopK(instanceId, limit,  isHighestFirst)
        } catch (e: DataAccessException) {
            logger.error("Redis access failed, falling back to SQL", e)
            //todo add redis downtime metric
            sqlRepository.getTopK(instanceId, limit, isHighestFirst)
        }
    }

    override suspend fun getUserRank(
        instanceId: String,
        userId: String,
        isHighestFirst: Boolean
    ): LeaderboardEntry? {
        return try {
            redisRepository.getUserRank(instanceId, userId, isHighestFirst)
                ?: run {
                    //todo add cache miss metric
                    sqlRepository.getUserRank(instanceId, userId, isHighestFirst)
                }
        } catch (e: DataAccessException) {
            logger.error("Redis access failed, falling back to SQL", e)
            //todo add redis downtime metric
            sqlRepository.getUserRank(instanceId, userId, isHighestFirst)
        }
    }

    override suspend fun saveScore(
        leaderboardInstanceId: String,
        userId: String,
        score: Double
    ) {
        //todo: this entire method should be retried in case of failure
        sqlRepository.saveScore(leaderboardInstanceId, userId, score)
        redisRepository.saveScore(leaderboardInstanceId, userId, score)
    }


}
