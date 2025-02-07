package com.poc.gateway.utils

import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service

@Service
class JwtSessionService(private val redisTemplate: StringRedisTemplate) {

    fun getSession(sessionId: String): SessionData? {
        val sessionData = redisTemplate.opsForHash<String, String>().entries(sessionId)
        return if (sessionData.isNotEmpty()) {
            SessionData(sessionData["accessToken"] ?: "", sessionData["refreshToken"] ?: "")
        } else {
            null
        }
    }
}

data class SessionData(val accessToken: String, val refreshToken: String)