package com.platforms.leaderboard.read.service

import com.platforms.leaderboard.config.service.ConfigService
import com.platforms.leaderboard.read.domain.LeaderboardEntry
import com.platforms.leaderboard.common.repository.CompositeLeaderboardRepository
import org.springframework.stereotype.Service

@Service
class LeaderboardServiceImpl(
    private val leaderboardReadRepository: CompositeLeaderboardRepository,
    private val configService: ConfigService
) : LeaderboardService {

    companion object {
        private const val MAX_TOP_LIMIT = 100
    }

    override suspend fun getTop(instanceId: String, limit: Int): List<LeaderboardEntry> {
        val safeLimit = limit.coerceIn(1, MAX_TOP_LIMIT)
        val config = configService.getLeaderboardConfig(instanceId)
        return leaderboardReadRepository.getTopK(instanceId, safeLimit, config.highestFirst)
    }

    override suspend fun getUserRank(userId: String, instanceId: String): LeaderboardEntry? {
        val config = configService.getLeaderboardConfig(instanceId)
        return leaderboardReadRepository.getUserRank(instanceId, userId, config.highestFirst)
            ?.let { it.copy(rank = it.rank?.plus(1)) } // Convert to 1-based rank
    }
}
