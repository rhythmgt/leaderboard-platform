package com.platforms.leaderboard.write.service

import com.platforms.leaderboard.configservice.dto.ScoringStrategyDto
import org.slf4j.LoggerFactory
import org.springframework.expression.ExpressionParser
import org.springframework.expression.spel.standard.SpelExpressionParser
import org.springframework.expression.spel.support.StandardEvaluationContext
import org.springframework.stereotype.Service

@Service
class ScoreCalculationService {
    private val logger = LoggerFactory.getLogger(javaClass)
    private val spelParser: ExpressionParser = SpelExpressionParser()

    fun calculateScore(features: Map<String, Any>, scoringStrategy: ScoringStrategyDto): Double {
        return try {
            val context = StandardEvaluationContext().apply {
                features.forEach { (key, value) ->
                    setVariable(key, value)
                }
            }
            
            val expression = spelParser.parseExpression(scoringStrategy.spelExpression)
            expression.getValue(context, Double::class.java) ?: 0.0
        } catch (e: Exception) {
            logger.error("Error calculating score with features: $features and strategy: $scoringStrategy", e)
            0.0
        }
    }
}
