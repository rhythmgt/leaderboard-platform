package com.platforms.leaderboard.read.repository

import com.platforms.leaderboard.read.domain.LeaderboardEntry
import org.springframework.stereotype.Repository

@Repository
class SqlLeaderboardReadRepository(
    private val userScoreRepository: UserScoreRepository
) : LeaderboardReadRepository {

    override suspend fun getTopK(
        instanceId: String,
        limit: Int,
        isHighestFirst: Boolean
    ): List<LeaderboardEntry> {
        return userScoreRepository.findTopScoresWithRank(
            leaderboardInstanceId = instanceId,
            limit = limit,
            offset = 0
        ).map { it.toLeaderboardEntry() }
    }

    override suspend fun getUserRank(
        instanceId: String,
        userId: String,
        isHighestFirst: Boolean
    ): LeaderboardEntry? {
        return userScoreRepository.findUserWithRank(instanceId, userId)?.toLeaderboardEntry()
    }
}
