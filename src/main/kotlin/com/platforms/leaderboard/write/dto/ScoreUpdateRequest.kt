package com.platforms.leaderboard.write.dto

data class ScoreUpdateRequest(
    val leaderboardInstanceId: String,
    val userId: String,
    val score: Double
)

data class ScoreUpdateResponse(
    val success: Boolean,
    val message: String = if (success) "Score updated successfully" else "Failed to update score"
)
