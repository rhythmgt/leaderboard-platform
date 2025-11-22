package com.platforms.leaderboard.configservice.dto

import com.platforms.leaderboard.configservice.constants.LeaderboardStatus
import com.platforms.leaderboard.configservice.constants.RankingOrder
import java.time.Instant

data class LeaderboardConfigDto(
    val id: String? = null,
    val tenantId: String,
    val version: Int = 1,
    val name: String,
    val featureConfig: List<FeatureConfigDto>,
    val rankingOrder: RankingOrder,
    val scoringStrategy: ScoringStrategyDto,
    val recurringConfig: RecurringConfigDto? = null,
    val status: LeaderboardStatus = LeaderboardStatus.DRAFT,
    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now(),
    val startTime: Instant,
    val endTime: Instant? = null
) {
    /**
     * Determines if a score should be considered higher than another score
     * based on the configured ranking order.
     * 
     * @param score1 The first score to compare
     * @param score2 The second score to compare
     * @return true if score1 is considered higher than score2 based on the ranking order
     */
    fun highestFirst(): Boolean {
        return rankingOrder == RankingOrder.HIGHEST_FIRST

    }
}
