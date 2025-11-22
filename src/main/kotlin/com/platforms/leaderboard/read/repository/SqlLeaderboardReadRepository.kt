package com.platforms.leaderboard.read.repository

import com.platforms.leaderboard.read.domain.LeaderboardEntry
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.awaitOneOrNull
import org.springframework.r2dbc.core.flow
import org.springframework.stereotype.Repository
import java.time.Instant

@Repository
class SqlLeaderboardReadRepository(
    private val userScoreRepository: UserScoreRepository,
    private val databaseClient: DatabaseClient
) : LeaderboardReadRepository {

    override suspend fun getTopK(
        instanceId: String,
        limit: Int,
        offset: Int,
        isHighestFirst: Boolean
    ): List<LeaderboardEntry> {
        val orderClause = if (isHighestFirst) "DESC" else "ASC"
        
        return databaseClient.sql("""
            WITH ranked_scores AS (
                SELECT 
                    user_id,
                    score,
                    ROW_NUMBER() OVER (ORDER BY score $orderClause) as rank
                FROM user_score
                WHERE leaderboard_instance_id = :instanceId
            )
            SELECT user_id, score, rank
            FROM ranked_scores
            WHERE rank BETWEEN :startRank AND :endRank
            ORDER BY rank
        """.trimIndent())
            .bind("instanceId", instanceId)
            .bind("startRank", offset + 1)
            .bind("endRank", offset + limit)
            .map { row ->
                LeaderboardEntry(
                    userId = row.get("user_id").toString(),
                    score = (row.get("score") as Number).toDouble(),
                    rank = (row.get("rank") as Number).toLong(),
                    additionalData = emptyMap()
                )
            }
            .all()
            .collectList()
            .awaitOneOrNull() ?: emptyList()
    }

    override suspend fun getUserRank(
        instanceId: String,
        userId: String,
        isHighestFirst: Boolean
    ): LeaderboardEntry? {
        val comparison = if (isHighestFirst) ">" else "<"
        
        return databaseClient.sql("""
            WITH user_rank AS (
                SELECT 
                    user_id,
                    score,
                    ROW_NUMBER() OVER (ORDER BY score $comparison) as rank
                FROM user_score
                WHERE leaderboard_instance_id = :instanceId
            )
            SELECT user_id, score, rank
            FROM user_rank
            WHERE user_id = :userId
        """.trimIndent())
            .bind("instanceId", instanceId)
            .bind("userId", userId)
            .map { row ->
                LeaderboardEntry(
                    userId = row.get("user_id").toString(),
                    score = (row.get("score") as Number).toDouble(),
                    rank = (row.get("rank") as Number).toLong(),
                    additionalData = emptyMap()
                )
            }
            .one()
            .awaitOneOrNull()
    }

    override suspend fun getAroundUser(
        instanceId: String,
        userId: String,
        limit: Int,
        isHighestFirst: Boolean
    ): List<LeaderboardEntry> {
        val orderClause = if (isHighestFirst) "DESC" else "ASC"
        val halfLimit = (limit - 1) / 2
        
        return databaseClient.sql("""
            WITH ranked_scores AS (
                SELECT 
                    user_id,
                    score,
                    ROW_NUMBER() OVER (ORDER BY score $orderClause) as rank
                FROM user_score
                WHERE leaderboard_instance_id = :instanceId
            ),
            user_rank AS (
                SELECT rank 
                FROM ranked_scores 
                WHERE user_id = :userId
            )
            SELECT user_id, score, rank
            FROM ranked_scores
            WHERE rank BETWEEN (
                SELECT GREATEST(1, rank - $halfLimit) FROM user_rank
            ) AND (
                SELECT LEAST((SELECT COUNT(*) FROM ranked_scores), rank + $halfLimit) FROM user_rank
            )
            ORDER BY rank
        """.trimIndent())
            .bind("instanceId", instanceId)
            .bind("userId", userId)
            .map { row ->
                LeaderboardEntry(
                    userId = row.get("user_id").toString(),
                    score = (row.get("score") as Number).toDouble(),
                    rank = (row.get("rank") as Number).toLong(),
                    additionalData = emptyMap()
                )
            }
            .all()
            .collectList()
            .awaitOneOrNull() ?: emptyList()
    }
    
    // For testing and data initialization
    suspend fun saveScore(instanceId: String, userId: String, score: Double) {
        userScoreRepository.findByLeaderboardInstanceIdAndUserId(instanceId, userId)
            .flatMap { existing ->
                userScoreRepository.save(
                    existing.copy(
                        score = score,
                        updatedAt = Instant.now()
                    )
                )
            }
            .switchIfEmpty(
                userScoreRepository.save(
                    UserScore(
                        leaderboardInstanceId = instanceId,
                        userId = userId,
                        score = score
                    )
                )
            )
            .block()
    }
}
