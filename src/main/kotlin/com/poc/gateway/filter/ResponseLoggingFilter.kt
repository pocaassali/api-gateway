package com.poc.gateway.filter

import org.slf4j.LoggerFactory
import org.springframework.cloud.gateway.filter.GatewayFilterChain
import org.springframework.cloud.gateway.filter.GlobalFilter
import org.springframework.http.HttpStatus
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

class ResponseLoggingFilter : GlobalFilter {

    private val logger = LoggerFactory.getLogger(ResponseLoggingFilter::class.java)

    override fun filter(exchange: ServerWebExchange, chain: GatewayFilterChain): Mono<Void> {
        val originalResponse = exchange.response

        return chain.filter(exchange.mutate().response(originalResponse).build())
            .doFinally {
                logResponse(originalResponse)
            }
    }

    private fun logResponse(response: ServerHttpResponse) {
        val status = response.statusCode ?: HttpStatus.INTERNAL_SERVER_ERROR
        logger.info("Response Status: $status, Response Body: $response")
    }
}