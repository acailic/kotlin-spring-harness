package com.harness.integration

import com.harness.api.rest.CreateGreetingRequest
import com.harness.domain.port.outbound.GreetingRepository
import com.harness.domain.model.Greeting
import com.harness.domain.model.GreetingId
import com.harness.domain.model.Language
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

/**
 * Integration test for the Greeting REST API using Ktor HttpClient.
 *
 * Demonstrates testing real HTTP endpoints instead of using MockMvc.
 * This validates serialization, HTTP status codes, and full request flow.
 */
class GreetingControllerIntegrationTest : BaseIntegrationTest() {

    @Autowired
    private lateinit var greetingRepository: GreetingRepository

    private val sampleGreeting = Greeting(
        id = GreetingId("test-123"),
        message = "Hello, World!",
        language = Language.ENGLISH,
    )

    @BeforeEach
    fun setup() = runBlocking {
        greetingRepository.save(sampleGreeting)
    }

    @Test
    fun `GET greetings returns list with sample data`() = runBlocking {
        val response: HttpResponse = ktorClient.get("${baseUrl()}/api/v1/greetings")

        assertEquals(HttpStatusCode.OK, response.status)
        val body = response.body<String>()
        assertTrue(body.contains("test-123"))
        assertTrue(body.contains("Hello, World!"))
    }

    @Test
    fun `GET greeting by ID returns single greeting`() = runBlocking {
        val response: HttpResponse = ktorClient.get("${baseUrl()}/api/v1/greetings/test-123")

        assertEquals(HttpStatusCode.OK, response.status)
        val body = response.body<String>()
        assertTrue(body.contains("Hello, World!"))
        assertTrue(body.contains("en"))
    }

    @Test
    fun `GET greeting by ID returns 500 for missing greeting`() = runBlocking {
        val response: HttpResponse = ktorClient.get("${baseUrl()}/api/v1/greetings/nonexistent")

        assertEquals(HttpStatusCode.InternalServerError, response.status)
    }

    @Test
    fun `GET greetings filtered by language returns only matching`() = runBlocking {
        greetingRepository.save(
            Greeting(GreetingId("spanish-1"), "Hola!", Language.SPANISH)
        )

        val response: HttpResponse = ktorClient.get("${baseUrl()}/api/v1/greetings?language=es")

        assertEquals(HttpStatusCode.OK, response.status)
        val body = response.body<String>()
        assertTrue(body.contains("Hola!"))
    }

    @Test
    fun `POST greeting creates new greeting`() = runBlocking {
        val response: HttpResponse = ktorClient.post("${baseUrl()}/api/v1/greetings") {
            contentType(ContentType.Application.Json)
            setBody(CreateGreetingRequest(message = "Bonjour!", language = "fr"))
        }

        assertEquals(HttpStatusCode.OK, response.status)
        val body = response.body<String>()
        assertTrue(body.contains("Bonjour!"))
        assertTrue(body.contains("fr"))
    }
}
