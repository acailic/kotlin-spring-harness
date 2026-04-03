package com.harness.domain.model

/**
 * Immutable domain entity representing a greeting.
 *
 * This class lives in the domain layer and must have ZERO dependencies
 * on Spring Framework or any infrastructure concern.
 */
data class Greeting(
    val id: GreetingId,
    val message: String,
    val language: Language,
)

/**
 * Value object for greeting identifiers.
 * Uses a type alias pattern for type safety without overhead.
 */
@JvmInline
value class GreetingId(val value: String) {
    init {
        require(value.isNotBlank()) { "GreetingId must not be blank" }
    }
}

/**
 * Supported greeting languages.
 * Using sealed interface for exhaustive when-expressions.
 */
enum class Language(val code: String, val displayName: String) {
    ENGLISH("en", "English"),
    SPANISH("es", "Spanish"),
    FRENCH("fr", "French"),
    GERMAN("de", "German"),
}
