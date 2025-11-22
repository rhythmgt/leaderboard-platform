package com.platforms.leaderboard.read.repository

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

@Table("user_score")
data class UserScoreEntity(
    @Id
    val id: Long? = null,
    val leaderboardInstanceId: String,
    val userId: String,
    val score: Double,
    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now()
)
