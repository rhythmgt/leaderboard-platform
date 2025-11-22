package com.platforms.leaderboard

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["com.platforms.leaderboard"])
class LeaderboardApplication

fun main(args: Array<String>) {
	runApplication<LeaderboardApplication>(*args)
}
