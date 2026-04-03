package com.harness.api.rest

import com.harness.api.rest.GreetingResponse.Companion.toResponse
import com.harness.domain.model.GreetingId
import com.harness.domain.model.Language
import com.harness.domain.port.inbound.GetGreetingUseCase
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

/**
 * REST controller for greeting operations.
 *
 * Depends on the inbound port [GetGreetingUseCase], not on
 * concrete services. This keeps the API layer decoupled from
 * the application layer's implementation details.
 */
@RestController
@RequestMapping("/api/v1/greetings")
class GreetingController(
    private val getGreetingUseCase: GetGreetingUseCase,
) {

    /**
     * Retrieve a greeting by ID.
     */
    @GetMapping("/{id}")
    suspend fun getById(@PathVariable id: String): ResponseEntity<GreetingResponse> {
        val greeting = getGreetingUseCase.getById(GreetingId(id))
        return ResponseEntity.ok(greeting.toResponse())
    }

    /**
     * Retrieve all greetings, optionally filtered by language.
     */
    @GetMapping
    suspend fun getAll(
        @RequestParam(required = false) language: String?,
    ): ResponseEntity<List<GreetingResponse>> {
        val lang = language?.let { Language.entries.find { l -> l.code == language } }
        val greetings = getGreetingUseCase.getAll(lang)
        return ResponseEntity.ok(greetings.map { it.toResponse() })
    }

    /**
     * Create a new greeting.
     */
    @PostMapping
    suspend fun create(
        @RequestBody request: CreateGreetingRequest,
    ): ResponseEntity<GreetingResponse> {
        val greeting = getGreetingUseCase.create(
            message = request.message,
            language = Language.entries.find { it.code == request.language }
                ?: Language.ENGLISH,
        )
        return ResponseEntity.ok(greeting.toResponse())
    }
}
