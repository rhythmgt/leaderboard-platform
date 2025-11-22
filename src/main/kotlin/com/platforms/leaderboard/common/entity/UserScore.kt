package com.platforms.leaderboard.common.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("user_score")
data class UserScore(
    @Id
    val id: Long? = null,

    @Column("leaderboard_instance_id")
    val leaderboardInstanceId: String,

    @Column("user_id")
    val userId: String,

    var score: Double,
) : BaseEntity()