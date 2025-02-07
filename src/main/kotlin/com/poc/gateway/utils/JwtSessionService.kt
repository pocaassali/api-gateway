package com.poc.gateway.utils

import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service
import java.time.Duration
import java.util.concurrent.TimeUnit

const val ACCESS_TOKEN_SESSION_KEY = "ACCESS_TOKEN"
const val DEFAULT_ACCESS_TOKEN_EXPIRATION = 1L
const val REFRESH_TOKEN_SESSION_KEY = "REFRESH_TOKEN"
const val DEFAULT_REFRESH_TOKEN_EXPIRATION = 7L

@Service
class JwtSessionService(private val redisTemplate: StringRedisTemplate) {

    /**
     * For 1 hour by default
     * */
    fun storeAccessToken(
        userId: String,
        token: String,
        expiration: Long = DEFAULT_ACCESS_TOKEN_EXPIRATION,
        timeUnit: TimeUnit = TimeUnit.HOURS
    ) = redisTemplate.opsForValue().set("$ACCESS_TOKEN_SESSION_KEY:$userId", token, expiration, timeUnit)


    /**
     * For 1 week by default
     * */
    fun storeRefreshToken(
        userId: String,
        token: String,
        expiration: Long = DEFAULT_REFRESH_TOKEN_EXPIRATION,
        timeUnit: TimeUnit = TimeUnit.DAYS
    ) = redisTemplate.opsForValue().set("$REFRESH_TOKEN_SESSION_KEY:$userId", token, expiration, timeUnit)


    fun getAccessToken(userId: String): String? {
        return redisTemplate.opsForValue().get("$ACCESS_TOKEN_SESSION_KEY:$userId")
    }

    fun getRefreshToken(userId: String): String? {
        return redisTemplate.opsForValue().get("$REFRESH_TOKEN_SESSION_KEY:$userId")
    }

    /*fun invalidateSession(sessionId: String) {
        redisTemplate.delete("session:$sessionId")
    }*/

    fun deleteTokens(userId: String) {
        redisTemplate.delete("$ACCESS_TOKEN_SESSION_KEY:$userId")
        redisTemplate.delete("$REFRESH_TOKEN_SESSION_KEY:$userId")
    }

    fun storeSession(sessionId: String, accessToken: String, refreshToken: String) {
        val sessionData = mapOf(
            "accessToken" to accessToken,
            "refreshToken" to refreshToken
        )
        redisTemplate.opsForHash<String, String>().putAll(sessionId, sessionData)
        redisTemplate.expire(sessionId, Duration.ofDays(7))
    }

    fun removeSession(sessionId: String) {
        redisTemplate.delete(sessionId)
    }

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