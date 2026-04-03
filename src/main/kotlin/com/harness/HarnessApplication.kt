package com.harness

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

/**
 * Entry point for the Kotlin Spring Harness application.
 *
 * This template demonstrates Hexagonal Architecture with AI-ready conventions.
 * See AGENTS.md for AI agent guidelines and coding standards.
 */
@SpringBootApplication
class HarnessApplication

fun main(args: Array<String>) {
    @Suppress("SpreadOperator") runApplication<HarnessApplication>(*args)
}
