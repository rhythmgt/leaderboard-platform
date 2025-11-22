package com.platforms.leaderboard.common.repository

import com.platforms.leaderboard.read.domain.UserScoreWithRank
import com.platforms.leaderboard.common.entity.UserScore
import kotlinx.coroutines.flow.Flow
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface UserScoreRepository : CoroutineCrudRepository<UserScore, Long> {

    @Query("""
        SELECT * FROM user_score 
        WHERE leaderboard_instance_id = :leaderboardInstanceId 
        AND user_id = :userId
    """)
    suspend fun findByLeaderboardInstanceIdAndUserId(
        leaderboardInstanceId: String, 
        userId: String
    ): UserScore?
    
    @Query("""
        SELECT us.*, 
               ROW_NUMBER() OVER (ORDER BY us.score DESC) as rank
        FROM user_score us
        WHERE us.leaderboard_instance_id = :leaderboardInstanceId
        ORDER BY us.score DESC
        LIMIT :limit OFFSET :offset
    """)
    suspend fun findTopScoresWithRank(
        leaderboardInstanceId: String,
        limit: Int,
        offset: Int
    ): List<UserScoreWithRank>
    
    @Query("""
        SELECT us.*, 
               ROW_NUMBER() OVER (ORDER BY us.score DESC) as rank
        FROM user_score us
        WHERE us.leaderboard_instance_id = :leaderboardInstanceId
        AND us.user_id = :userId
    """)
    suspend fun findUserWithRank(
        leaderboardInstanceId: String,
        userId: String
    ): UserScoreWithRank?

}
