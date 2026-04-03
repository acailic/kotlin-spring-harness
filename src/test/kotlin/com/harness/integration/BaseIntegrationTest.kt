package com.harness.integration

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles

/**
 * Base class for integration tests using Ktor HttpClient.
 *
 * Why Ktor instead of MockMvc?
 * - Tests real HTTP protocol (headers, status codes, serialization)
 * - Idiomatic Kotlin with coroutines support
 * - Lightweight and fast
 * - Framework-agnostic client (not tied to Spring internals)
 *
 * Extend this class for all integration tests that need to call
 * REST endpoints through actual HTTP.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Import(TestConfig::class)
abstract class BaseIntegrationTest {

    @LocalServerPort
    protected var port: Int = 0

    @Autowired
    protected lateinit var ktorClient: HttpClient

    protected fun baseUrl(): String = "http://localhost:$port"

    companion object {
        /**
         * Create a Ktor HttpClient configured for JSON content negotiation.
         * Used by the Spring test configuration.
         */
        fun createTestClient(): HttpClient = HttpClient(CIO) {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                    prettyPrint = false
                })
            }
            engine {
                requestTimeout = 5_000
            }
        }
    }
}
