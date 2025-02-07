package com.poc.gateway.config

import org.springframework.http.HttpMethod
import org.springframework.http.HttpMethod.GET
import org.springframework.http.HttpMethod.POST

//TODO : Migrate to persistence solution
enum class Backend(
    val id : String,
    val path : String,
    val uri : String,
    val publicEndpoints: Map<HttpMethod,Set<String>>,
) {
    SERVICE_RESOURCE(
        id = "resource",
        path = "/svc/resources/**",
        uri = "http://localhost:8081",
        publicEndpoints = mapOf(
            GET to setOf("/svc/resources/persons")
        )
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