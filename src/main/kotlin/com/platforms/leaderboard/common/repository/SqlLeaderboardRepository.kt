package com.platforms.leaderboard.common.repository

import com.platforms.leaderboard.read.domain.LeaderboardEntry
import com.platforms.leaderboard.common.entity.UserScore
import org.springframework.stereotype.Repository

@Repository
class SqlLeaderboardRepository(
    private val userScoreRepository: UserScoreRepository
) : LeaderboardRepository {

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

    override suspend fun saveScore(leaderboardInstanceId: String, userId: String, score: Double) {
        val userScore = userScoreRepository.findByLeaderboardInstanceIdAndUserId(leaderboardInstanceId, userId)
            ?: UserScore(
                leaderboardInstanceId = leaderboardInstanceId,
                userId = userId,
                score = score
            )
        userScore.score = score
        userScoreRepository.save(userScore)
    }
}
