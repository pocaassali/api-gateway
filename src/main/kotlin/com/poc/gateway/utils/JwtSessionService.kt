package com.poc.gateway.utils

import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service

@Service
class JwtSessionService(private val redisTemplate: StringRedisTemplate) {

    /*fun getSession(sessionId: String): SessionData? {
        return try {
            val accessToken = redisTemplate.opsForHash<String, String>().get(sessionId, "accessToken")
            val refreshToken = redisTemplate.opsForHash<String, String>().get(sessionId, "refreshToken")

            if (!accessToken.isNullOrEmpty() && !refreshToken.isNullOrEmpty()) {
                SessionData(accessToken, refreshToken)
            } else {
                null
            }
        } catch (ex: Exception) {
            println("Error while fetching session data from Redis: ${ex.message}")
            null
        }
    }*/

    fun getSession(sessionId: String): Session? {
        val sessionData = redisTemplate.opsForHash<String, String>().entries(sessionId)
        return if (sessionData.isNotEmpty()) {
            Session(sessionId, sessionData)
        } else {
            null
        }
    }

}

data class Session(val id: String, val data: Map<String, String>)