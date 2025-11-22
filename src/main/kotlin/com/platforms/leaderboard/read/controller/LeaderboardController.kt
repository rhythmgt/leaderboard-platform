package com.platforms.leaderboard.read.controller

import com.platforms.leaderboard.read.domain.LeaderboardEntry
import com.platforms.leaderboard.read.repository.LeaderboardReadRepository
import com.platforms.leaderboard.read.repository.SqlLeaderboardReadRepository
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/leaderboard")
class LeaderboardController(
    private val leaderboardReadRepository: LeaderboardReadRepository,
    private val sqlLeaderboardReadRepository: SqlLeaderboardReadRepository
) {

    // Using Repository pattern (simpler queries)
    @GetMapping("/repository/top")
    suspend fun getTopKWithRepository(
        @RequestParam instanceId: String,
        @RequestParam(defaultValue = "10") limit: Int,
        @RequestParam(defaultValue = "0") offset: Int,
        @RequestParam(defaultValue = "true") highestFirst: Boolean
    ): List<LeaderboardEntry> {
        return leaderboardReadRepository.getTopK(instanceId, limit, offset, highestFirst)
    }

    // Using DatabaseClient (complex optimized queries)
    @GetMapping("/optimized/top")
    suspend fun getTopKOptimized(
        @RequestParam instanceId: String,
        @RequestParam(defaultValue = "10") limit: Int,
        @RequestParam(defaultValue = "0") offset: Int,
        @RequestParam(defaultValue = "true") highestFirst: Boolean
    ): List<LeaderboardEntry> {
        return sqlLeaderboardReadRepository.getTopK(instanceId, limit, offset, highestFirst)
    }

    // Get user rank with repository
    @GetMapping("/repository/rank/{userId}")
    suspend fun getUserRankWithRepository(
        @PathVariable userId: String,
        @RequestParam instanceId: String,
        @RequestParam(defaultValue = "true") highestFirst: Boolean
    ): LeaderboardEntry? {
        return leaderboardReadRepository.getUserRank(instanceId, userId, highestFirst)
    }

    // Get user rank with optimized query
    @GetMapping("/optimized/rank/{userId}")
    suspend fun getUserRankOptimized(
        @PathVariable userId: String,
        @RequestParam instanceId: String,
        @RequestParam(defaultValue = "true") highestFirst: Boolean
    ): LeaderboardEntry? {
        return sqlLeaderboardReadRepository.getUserRank(instanceId, userId, highestFirst)
    }

    // Get users around a specific user with repository
    @GetMapping("/repository/around/{userId}")
    suspend fun getAroundUserWithRepository(
        @PathVariable userId: String,
        @RequestParam instanceId: String,
        @RequestParam(defaultValue = "5") limit: Int,
        @RequestParam(defaultValue = "true") highestFirst: Boolean
    ): List<LeaderboardEntry> {
        return leaderboardReadRepository.getAroundUser(instanceId, userId, limit, highestFirst)
    }

    // Get users around a specific user with optimized query
    @GetMapping("/optimized/around/{userId}")
    suspend fun getAroundUserOptimized(
        @PathVariable userId: String,
        @RequestParam instanceId: String,
        @RequestParam(defaultValue = "5") limit: Int,
        @RequestParam(defaultValue = "true") highestFirst: Boolean
    ): List<LeaderboardEntry> {
        return sqlLeaderboardReadRepository.getAroundUser(instanceId, userId, limit, highestFirst)
    }
}
