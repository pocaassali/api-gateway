package com.poc.gateway.filter

import com.poc.gateway.utils.JwtSessionService
import org.springframework.cloud.gateway.filter.GatewayFilter
import org.springframework.cloud.gateway.filter.GatewayFilterChain
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Component
class AuthFilter(
    private val jwtSessionService: JwtSessionService
) : GatewayFilter {

    override fun filter(exchange: ServerWebExchange, chain: GatewayFilterChain): Mono<Void> {
        val request = exchange.request

        val sessionId = request.cookies["SESSION_ID"]?.firstOrNull()?.value
            ?: return this.onError(exchange, "Cookie session is missing !")

        val accessToken = jwtSessionService.getSession(sessionId)?.accessToken

        if (accessToken.isNullOrEmpty()) {
            return this.onError(exchange, "Access token is null or empty!")
        }

        val modifiedRequest = request.mutate()
            .header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
            .build()

        val modifiedExchange = exchange.mutate().request(modifiedRequest).build()
        return chain.filter(modifiedExchange)
    }

    private fun onError(exchange: ServerWebExchange, err: String): Mono<Void> {
        println(err)
        val response = exchange.response
        response.setStatusCode(HttpStatus.UNAUTHORIZED)
        return response.setComplete()
    }
}