package com.platforms.leaderboard.write.dto

/**
 * Request for updating a score with features that will be used to calculate the score
 */
data class FeatureBasedScoreUpdateRequest(
    val leaderboardInstanceId: String,
    val userId: String,
    val features: Map<String, Any>
)

/**
 * Response for score update operations
 */
data class ScoreUpdateResponse(
    val success: Boolean,
    val message: String = if (success) "Score updated successfully" else "Failed to update score",
    val score: Double? = null
)
