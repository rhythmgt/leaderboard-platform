package com.platforms.leaderboard.read.repository

import com.platforms.leaderboard.read.entity.UserScore
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
interface UserScoreRepository : ReactiveCrudRepository<UserScore, Long> {
    
    fun findByLeaderboardInstanceId(leaderboardInstanceId: String): Flux<UserScore>
    
    @Query("""
        SELECT * FROM user_score 
        WHERE leaderboard_instance_id = :leaderboardInstanceId 
        AND user_id = :userId
    """)
    fun findByLeaderboardInstanceIdAndUserId(
        leaderboardInstanceId: String, 
        userId: String
    ): Mono<UserScore>
    
    @Query("""
        SELECT us.*, 
               ROW_NUMBER() OVER (ORDER BY us.score DESC) as rank
        FROM user_score us
        WHERE us.leaderboard_instance_id = :leaderboardInstanceId
        ORDER BY us.score DESC
        LIMIT :limit OFFSET :offset
    """)
    fun findTopScores(
        leaderboardInstanceId: String,
        limit: Int,
        offset: Int
    ): Flux<Map<String, Any>>
    
    @Query("""
        WITH ranked_scores AS (
            SELECT user_id, score, 
                   ROW_NUMBER() OVER (ORDER BY score DESC) as rank
            FROM user_score
            WHERE leaderboard_instance_id = :leaderboardInstanceId
        )
        SELECT * FROM ranked_scores
        WHERE user_id = :userId
    """)
    fun findUserRank(
        leaderboardInstanceId: String,
        userId: String
    ): Mono<Map<String, Any>>
    
    @Query("""
        WITH ranked_scores AS (
            SELECT user_id, score, 
                   ROW_NUMBER() OVER (ORDER BY score DESC) as rank
            FROM user_score
            WHERE leaderboard_instance_id = :leaderboardInstanceId
        ),
        user_rank AS (
            SELECT rank FROM ranked_scores WHERE user_id = :userId
        )
        SELECT * FROM ranked_scores
        WHERE rank BETWEEN (SELECT rank - :halfLimit FROM user_rank) 
                      AND (SELECT rank + :halfLimit FROM user_rank)
        ORDER BY rank
    """)
    fun findAroundUser(
        leaderboardInstanceId: String,
        userId: String,
        halfLimit: Int
    ): Flux<Map<String, Any>>
}
