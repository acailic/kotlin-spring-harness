package com.harness.domain.port.inbound

import com.harness.domain.model.Greeting
import com.harness.domain.model.GreetingId
import com.harness.domain.model.Language

/**
 * Inbound port defining the use case for retrieving greetings.
 *
 * This is the contract that the application layer fulfills.
 * API/controllers depend on this interface, not on concrete services.
 */
interface GetGreetingUseCase {

    /**
     * Retrieve a greeting by its identifier.
     *
     * @param id the unique greeting identifier
     * @return the greeting
     * @throws NoSuchElementException if the greeting does not exist
     */
    suspend fun getById(id: GreetingId): Greeting

    /**
     * Retrieve all greetings, optionally filtered by language.
     *
     * @param language optional language filter
     * @return list of greetings
     */
    suspend fun getAll(language: Language? = null): List<Greeting>

    /**
     * Create a new greeting with a default message.
     *
     * @param message the greeting message
     * @param language the greeting language
     * @return the created greeting
     */
    suspend fun create(message: String, language: Language): Greeting
}
