package com.poc.gateway.config

import org.springframework.http.HttpMethod
import org.springframework.http.HttpMethod.GET

//TODO : Migrate to persistence solution
enum class Backend(
    val id: String,
    val path: String,
    val uri: String,
    val publicEndpoints: Map<HttpMethod, Set<String>>,
) {
    SERVICE_USERS(
        id = "user",
        path = "/svc/users/**",
        uri = "http://localhost:8081",
        publicEndpoints = emptyMap()
    ),
    AUTH_SERVER(
        id = "auth",
        path = "/svc/auth/**",
        uri = "http://localhost:8081",
        publicEndpoints = mapOf(
            GET to setOf("/svc/auth/login")
        )
    );
}