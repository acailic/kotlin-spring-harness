package com.harness.api.rest

import com.harness.domain.model.Greeting
import kotlinx.serialization.Serializable

/**
 * REST response DTO for greeting data.
 *
 * Separates the API representation from the domain model.
 * This class may use Jackson annotations (infrastructure concern)
 * which is why it lives in the API layer, not the domain layer.
 */
data class GreetingResponse(
    val id: String,
    val message: String,
    val language: String,
    val languageDisplayName: String,
) {
    companion object {
        fun Greeting.toResponse(): GreetingResponse = GreetingResponse(
            id = id.value,
            message = message,
            language = language.code,
            languageDisplayName = language.displayName,
        )
    }
}

/**
 * REST request DTO for creating a greeting.
 */
@Serializable
data class CreateGreetingRequest(
    val message: String,
    val language: String = "en",
)
