package com.harness.application

import com.harness.domain.model.Greeting
import com.harness.domain.model.GreetingId
import com.harness.domain.model.Language
import com.harness.domain.port.inbound.GetGreetingUseCase
import com.harness.domain.port.outbound.GreetingRepository
import org.springframework.stereotype.Service
import java.util.UUID

/**
 * Application service implementing the greeting use case.
 *
 * Orchestrates domain logic by coordinating between inbound ports
 * (use cases) and outbound ports (repositories). This layer may
 * use Spring annotations for dependency injection.
 */
@Service
class GreetingService(
    private val greetingRepository: GreetingRepository,
) : GetGreetingUseCase {

    override suspend fun getById(id: GreetingId): Greeting {
        return greetingRepository.findById(id)
            ?: throw NoSuchElementException("Greeting not found: ${id.value}")
    }

    override suspend fun getAll(language: Language?): List<Greeting> {
        return greetingRepository.findAll(language)
    }

    override suspend fun create(message: String, language: Language): Greeting {
        val greeting = Greeting(
            id = GreetingId(UUID.randomUUID().toString()),
            message = message,
            language = language,
        )
        return greetingRepository.save(greeting)
    }
}
