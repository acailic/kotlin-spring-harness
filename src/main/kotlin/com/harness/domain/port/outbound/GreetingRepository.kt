package com.harness.domain.port.outbound

import com.harness.domain.model.Greeting
import com.harness.domain.model.GreetingId
import com.harness.domain.model.Language

/**
 * Outbound port for greeting persistence.
 *
 * Implemented by infrastructure adapters. The domain layer defines
 * WHAT it needs, not HOW it's implemented.
 */
interface GreetingRepository {

    /**
     * Find a greeting by its unique identifier.
     *
     * @param id the greeting identifier
     * @return the greeting, or null if not found
     */
    suspend fun findById(id: GreetingId): Greeting?

    /**
     * Find all greetings, optionally filtered by language.
     *
     * @param language optional language filter
     * @return list of matching greetings
     */
    suspend fun findAll(language: Language? = null): List<Greeting>

    /**
     * Persist a new greeting.
     *
     * @param greeting the greeting to save
     * @return the saved greeting with generated identifier
     */
    suspend fun save(greeting: Greeting): Greeting
}
