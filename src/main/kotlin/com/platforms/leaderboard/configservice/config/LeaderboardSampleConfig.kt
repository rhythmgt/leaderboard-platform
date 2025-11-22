package com.platforms.leaderboard.configservice.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import com.platforms.leaderboard.configservice.constants.LeaderboardStatus
import com.platforms.leaderboard.configservice.dto.LeaderboardConfigDto
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.Resource
import org.springframework.util.FileCopyUtils
import java.io.InputStreamReader

@Configuration
class LeaderboardSampleConfig(
    @Value("classpath:sample-leaderboard-config.json")
    private val sampleConfigResource: Resource
) {

    @Bean
    fun objectMapper(): ObjectMapper {
        return ObjectMapper()
            .registerModule(KotlinModule.Builder().build())
            .registerModule(JavaTimeModule())
    }

    @Bean
    fun sampleLeaderboardConfig(objectMapper: ObjectMapper): LeaderboardConfigDto {
        val json = FileCopyUtils.copyToString(InputStreamReader(sampleConfigResource.inputStream))
        return objectMapper.readValue(json)
    }
}
