package com.platforms.leaderboard.configservice.dto

import com.platforms.leaderboard.configservice.constants.FeatureDataType

data class FeatureConfigDto(
    val name: String,
    val dataType: FeatureDataType,
    val required: Boolean = false
)
