package com.platforms.leaderboard.write.controller

import com.platforms.leaderboard.write.dto.ScoreUpdateRequest
import com.platforms.leaderboard.write.dto.ScoreUpdateResponse
import com.platforms.leaderboard.write.service.LeaderboardWriteService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/leaderboard/write")
class LeaderboardWriteController(
    private val leaderboardWriteService: LeaderboardWriteService
) {

    @PostMapping("/score")
    suspend fun updateScore(
        @RequestBody request: ScoreUpdateRequest
    ): ResponseEntity<ScoreUpdateResponse> {
        leaderboardWriteService.updateScore(
            leaderboardInstanceId = request.leaderboardInstanceId,
            userId = request.userId,
            score = request.score
        )
        
        return ResponseEntity.ok(ScoreUpdateResponse(true))
    }
}
