package com.harness.unit.domain

import com.harness.domain.model.Greeting
import com.harness.domain.model.GreetingId
import com.harness.domain.model.Language
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

/**
 * Unit tests for domain models.
 *
 * Domain tests must have ZERO Spring dependencies.
 * They test pure business logic in isolation.
 */
class GreetingTest {

    @Test
    fun `GreetingId rejects blank values`() {
        assertThrows(IllegalArgumentException::class.java) {
            GreetingId("")
        }
        assertThrows(IllegalArgumentException::class.java) {
            GreetingId("   ")
        }
    }

    @Test
    fun `GreetingId accepts valid values`() {
        val id = GreetingId("valid-id")
        assertEquals("valid-id", id.value)
    }

    @Test
    fun `Greeting data class equality works correctly`() {
        val greeting1 = Greeting(
            id = GreetingId("1"),
            message = "Hello",
            language = Language.ENGLISH,
        )
        val greeting2 = Greeting(
            id = GreetingId("1"),
            message = "Hello",
            language = Language.ENGLISH,
        )

        assertEquals(greeting1, greeting2)
    }

    @Test
    fun `Language enum has correct codes`() {
        assertEquals("en", Language.ENGLISH.code)
        assertEquals("es", Language.SPANISH.code)
        assertEquals("fr", Language.FRENCH.code)
        assertEquals("de", Language.GERMAN.code)
    }
}
