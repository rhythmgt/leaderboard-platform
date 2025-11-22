package com.platforms.leaderboard.read.domain

data class LeaderboardEntry(
    val userId: String,
    val score: Double?,
    val rank: Long? = null,
    val additionalData: Map<String, Any> = emptyMap()
)
