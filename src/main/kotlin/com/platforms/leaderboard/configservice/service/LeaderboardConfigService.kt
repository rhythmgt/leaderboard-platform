package com.platforms.leaderboard.configservice.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import com.platforms.leaderboard.configservice.dto.LeaderboardConfigDto
import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.Resource
import org.springframework.stereotype.Service
import org.springframework.util.FileCopyUtils
import java.io.InputStreamReader

@Service
class LeaderboardConfigService(
    @Value("classpath:sample-leaderboard-config.json")
    private val sampleConfigResource: Resource,
    private val objectMapper: ObjectMapper = ObjectMapper()
        .registerModule(KotlinModule.Builder().build())
        .registerModule(JavaTimeModule())
) {
    
    private lateinit var sampleConfig: LeaderboardConfigDto
    
    @PostConstruct
    fun init() {
        sampleConfig = loadSampleConfig()
    }
    
    /**
     * Loads the sample leaderboard configuration from the JSON file
     */
    fun getSampleConfig(): LeaderboardConfigDto = sampleConfig
    
    /**
     * Loads a leaderboard configuration from a JSON string
     */
    fun loadConfigFromJson(json: String): LeaderboardConfigDto {
        return objectMapper.readValue(json)
    }
    
    /**
     * Converts a leaderboard configuration to JSON string
     */
    fun convertToJson(config: LeaderboardConfigDto): String {
        return objectMapper.writeValueAsString(config)
    }
    
    private fun loadSampleConfig(): LeaderboardConfigDto {
        val json = FileCopyUtils.copyToString(InputStreamReader(sampleConfigResource.inputStream))
        return loadConfigFromJson(json)
    }
}
