package com.platforms.leaderboard.read.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

@Table("user_score")
data class UserScore(
    @Id
    val id: Long? = null,
    
    @Column("leaderboard_instance_id")
    val leaderboardInstanceId: String,
    
    @Column("user_id")
    val userId: String,
    
    val score: Double = 0.0,
    
    @Column("created_at")
    val createdAt: Instant = Instant.now(),
    
    @Column("updated_at")
    var updatedAt: Instant = Instant.now()
) {
    fun withUpdatedAt(updatedAt: Instant): UserScore {
        return this.copy(updatedAt = updatedAt)
    }
    
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UserScore

        if (leaderboardInstanceId != other.leaderboardInstanceId) return false
        if (userId != other.userId) return false

        return true
    }

    override fun hashCode(): Int {
        var result = leaderboardInstanceId.hashCode()
        result = 31 * result + userId.hashCode()
        return result
    }

    override fun toString(): String {
        return "UserScore(id=$id, leaderboardInstanceId='$leaderboardInstanceId', userId='$userId', " +
                "score=$score, createdAt=$createdAt, updatedAt=$updatedAt)"
    }
}
