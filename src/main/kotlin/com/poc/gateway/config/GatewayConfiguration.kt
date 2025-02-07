package com.poc.gateway.config

import com.poc.gateway.filter.AuthFilter
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.cloud.gateway.route.RouteLocator
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class GatewayConfiguration(
    private val authFilter: AuthFilter
) {

    @Bean
    fun routeLocator(builder: RouteLocatorBuilder): RouteLocator {
        return builder.routes().also { routes ->
            Backend.entries.forEach { backend ->
                backend.publicEndpoints.forEach { (method, paths) ->
                    paths.forEach { path ->
                        routes.route("${backend.id}-${method.name()}-${path}") {
                            it.path(path)
                                .and()
                                .method(method)
                                .uri(backend.uri)
                        }
                    }
                }

                routes.route("${backend.id}-protected") {
                    it.path(backend.path)
                        .filters { f -> f.filters(authFilter) }
                        .uri(backend.uri)
                }
            }
        }.build().also {
            it.routes.subscribe { route ->
                LOGGER.info("Route: ${route.id}, Path: ${route.predicate}, URI: ${route.uri}")
            }
        }
    }


    companion object {
        val LOGGER: Logger = LoggerFactory.getLogger(GatewayConfiguration::class.java)
    }
}
