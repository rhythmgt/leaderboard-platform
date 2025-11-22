package com.platforms.leaderboard.configservice.dto

import java.time.Instant

data class RecurringConfigDto(
    val cronExpression: String,
    val recurringEndTime: Instant?
)
