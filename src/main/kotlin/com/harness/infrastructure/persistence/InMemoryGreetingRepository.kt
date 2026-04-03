package com.harness.infrastructure.persistence

import com.harness.domain.model.Greeting
import com.harness.domain.model.GreetingId
import com.harness.domain.model.Language
import com.harness.domain.port.outbound.GreetingRepository
import org.springframework.stereotype.Repository
import java.util.concurrent.ConcurrentHashMap

/**
 * In-memory implementation of [GreetingRepository].
 *
 * Suitable for development and testing. Replace with a real database
 * adapter (e.g., JPA, Exposed) in production.
 */
@Repository
class InMemoryGreetingRepository : GreetingRepository {

    private val store = ConcurrentHashMap<GreetingId, Greeting>()

    override suspend fun findById(id: GreetingId): Greeting? = store[id]

    override suspend fun findAll(language: Language?): List<Greeting> {
        val all = store.values.toList()
        return if (language != null) {
            all.filter { it.language == language }
        } else {
            all
        }
    }

    override suspend fun save(greeting: Greeting): Greeting {
        store[greeting.id] = greeting
        return greeting
    }
}
