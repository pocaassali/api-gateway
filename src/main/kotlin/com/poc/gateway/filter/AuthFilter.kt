package com.poc.gateway.filter

import com.poc.gateway.utils.JwtSessionService
import org.springframework.cloud.gateway.filter.GatewayFilter
import org.springframework.cloud.gateway.filter.GatewayFilterChain
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Component
class AuthFilter(
    private val jwtSessionService: JwtSessionService
) : GatewayFilter {

    override fun filter(exchange: ServerWebExchange, chain: GatewayFilterChain): Mono<Void> {
        val sessionId = extractSessionIdFromCookie(exchange)
            ?: return respondWithError(exchange, "Cookie is missing!")

        val session = jwtSessionService.getSession(sessionId)
            ?: return respondWithError(exchange, "No session found!")

        val accessToken = session.data["accessToken"]
            ?: return respondWithError(exchange, "No access token found!")

        val modifiedExchange = exchange.mutateWithAuthorization(accessToken)
        return chain.filter(modifiedExchange)
    }

    private fun extractSessionIdFromCookie(exchange: ServerWebExchange): String? {
        return exchange.request.cookies["SESSION_ID"]?.firstOrNull()?.value
    }

    private fun respondWithError(exchange: ServerWebExchange, errorMessage: String): Mono<Void> {
        val response = exchange.response
        response.setStatusCode(HttpStatus.UNAUTHORIZED)
        response.headers.contentType = MediaType.APPLICATION_JSON

        val errorResponse = """{"message": "$errorMessage"}"""
        val dataBuffer = exchange.response.bufferFactory().wrap(errorResponse.toByteArray())

        return response.writeWith(Mono.just(dataBuffer))
    }

    private fun ServerWebExchange.mutateWithAuthorization(accessToken: String): ServerWebExchange {
        val modifiedRequest = this.request.mutate()
            .header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
            .build()
        return this.mutate().request(modifiedRequest).build()
    }

}