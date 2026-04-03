package com.harness.integration

import io.ktor.client.HttpClient
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean

/**
 * Test configuration providing the Ktor HttpClient bean.
 *
 * This replaces any production HTTP client configuration
 * with a test-optimized client using CIO engine.
 */
@TestConfiguration
class TestConfig {

    @Bean
    fun ktorClient(): HttpClient = BaseIntegrationTest.createTestClient()
}
