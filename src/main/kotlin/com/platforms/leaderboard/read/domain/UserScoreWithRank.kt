package com.platforms.leaderboard.read.domain

import com.platforms.leaderboard.read.entity.UserScore

data class UserScoreWithRank(
    val userScore: UserScore,
    val rank: Long
) {
    fun toLeaderboardEntry(): LeaderboardEntry = LeaderboardEntry(
        userId = userScore.userId,
        score = userScore.score,
        rank = rank,
        additionalData = emptyMap()
    )
}
