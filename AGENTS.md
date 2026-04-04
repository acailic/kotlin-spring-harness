# AI Agent Guidelines — Kotlin Spring Harness

> **This file is the system prompt for AI coding assistants** (Cursor, GitHub Copilot, Claude, etc.).
> It ensures AI-generated code follows the project's architecture, conventions, and quality standards.

---

## Technology Stack

| Technology | Version | Purpose |
|---|---|---|
| Kotlin | 2.1.x | Primary language |
| Java | 21 | Runtime (JVM) |
| Spring Boot | 3.4.x | Application framework |
| Gradle | 8.12 (Kotlin DSL) | Build system |
| Detekt | 1.23.x | Static analysis |
| ArchUnit | 1.4.x | Architecture tests |
| Ktor Client | 3.0.x | Integration testing |
| JUnit 5 | — | Test framework |

---

## Architecture: Hexagonal (Ports & Adapters)

```
┌─────────────────────────────────────────────────┐
│                    API Layer                      │
│         Controllers, DTOs, Routes                │
│     Depends on: domain.port.inbound              │
├─────────────────────────────────────────────────┤
│                Application Layer                  │
│        Services, Use Case Implementations        │
│     Depends on: domain.port.inbound/outbound     │
├─────────────────────────────────────────────────┤
│                  Domain Layer                     │
│    Models, Value Objects, Port Interfaces        │
│     Depends on: NOTHING (pure Kotlin)            │
├─────────────────────────────────────────────────┤
│              Infrastructure Layer                 │
│     Repository Implementations, External APIs    │
│     Depends on: domain.port.outbound             │
└─────────────────────────────────────────────────┘
```

### Layer Dependency Rules

1. **Domain** has ZERO external dependencies. No Spring, no Jackson, no infrastructure.
2. **Application** implements inbound ports and uses outbound ports. May use `@Service`.
3. **Infrastructure** implements outbound ports. May use `@Repository`, `@Component`.
4. **API** calls inbound ports. May use `@RestController`, `@GetMapping`, etc.
5. **API** must NEVER directly access infrastructure.

---

## Coding Rules

### DO

- **Use immutable data classes** for all domain models and DTOs
- **Prefer `val` over `var`** everywhere. Mutable state is a bug factory.
- **Use `suspend` functions** for all I/O operations (database, network, file)
- **Use Kotlin Sequences** instead of Java Streams: `list.map { }` not `list.stream().map()`
- **Use `when` expressions** instead of `if-else` chains for type-safe branching
- **Use named parameters** in function calls with more than 1 argument
- **Use string templates** (`"Hello $name"`) not string concatenation
- **Use value classes** (`@JvmInline value class`) for type-safe identifiers
- **Use sealed interfaces/classes** for constrained type hierarchies
- **Write KDoc** for all public classes and functions
- **Prefer constructor injection** over field injection (`@Autowired`)
- **Use coroutines** (`kotlinx.coroutines`) for async operations, not `CompletableFuture`

### DO NOT

- **Do not use `var`** in data classes or domain models
- **Do not use Java Streams** (`stream()`, `collect()`, `mapToObj()`). Use Kotlin sequences/functions.
- **Do not use `!!` (non-null assertion)** unless absolutely unavoidable. Design your types correctly instead.
- **Do not import `java.util.stream`** or `java.util.Optional`. Use Kotlin's nullability and collections.
- **Do not add Spring annotations** to domain layer classes (`domain/` package)
- **Do not use `@Autowired` on fields**. Use constructor injection.
- **Do not catch generic `Exception`** or `Throwable`. Catch specific exceptions.
- **Do not use `println()`** or `System.out.println()`. Use a logging framework (SLF4J).
- **Do not return `null`** from functions. Use empty collections or `Optional`/nullable types.
- **Do not create God classes**. Each class has one responsibility.
- **Do not use `lateinit`** unless required by Spring DI. Prefer constructor injection.
- **Do not put business logic** in controllers or DTOs.
- **Do not access infrastructure** directly from controllers. Go through application services.

---

## Testing Standards

### Integration Tests (Ktor HttpClient)

Integration tests use **Ktor HttpClient** instead of MockMvc:

```kotlin
class MyControllerIntegrationTest : BaseIntegrationTest() {

    @Test
    fun `GET endpoint returns expected data`() = runBlocking {
        val response = ktorClient.get("${baseUrl()}/api/v1/resource")
        assertEquals(HttpStatusCode.OK, response.status)
    }
}
```

### Unit Tests

- Domain tests have **ZERO** Spring dependencies
- Use `runBlocking` or `runTest` for coroutine testing
- Test boundary conditions, not just happy paths

### Architecture Tests

- Located in `src/test/kotlin/.../archunit/`
- Run on every build — **do not skip or disable**
- If an architecture test fails, fix the architecture, not the test

---

## File Naming Conventions

| Type | Pattern | Example |
|---|---|---|
| Controller | `{Name}Controller.kt` | `GreetingController.kt` |
| Service | `{Name}Service.kt` | `GreetingService.kt` |
| Repository (interface) | `{Name}Repository.kt` | `GreetingRepository.kt` |
| Repository (impl) | `{Name}{Type}Repository.kt` | `InMemoryGreetingRepository.kt` |
| Domain Model | `{Name}.kt` | `Greeting.kt` |
| DTO | `{Name}Response.kt` / `{Name}Request.kt` | `GreetingResponse.kt` |
| Use Case (interface) | `{Name}UseCase.kt` | `GetGreetingUseCase.kt` |
| Test | `{Name}Test.kt` | `GreetingTest.kt` |
| Integration Test | `{Name}IntegrationTest.kt` | `GreetingControllerIntegrationTest.kt` |

---

## Package Structure

```
com.harness/
├── config/           # Spring @Configuration (may use Spring)
├── domain/           # PURE KOTLIN — NO Spring imports allowed
│   ├── model/        # Domain entities, value objects, enums
│   └── port/         # Port interfaces
│       ├── inbound/  # Use case interfaces (called by API/application)
│       └── outbound/ # Repository interfaces (implemented by infrastructure)
├── application/      # Use case implementations (may use Spring DI)
├── infrastructure/   # Outbound adapters (may use Spring, DB drivers, etc.)
│   └── persistence/  # Repository implementations
└── api/              # REST controllers and DTOs (may use Spring Web)
    └── rest/         # @RestController classes
```

---

## When Adding New Features

1. **Define the domain model** in `domain/model/` — pure Kotlin, immutable
2. **Define outbound ports** in `domain/port/outbound/` — interfaces only
3. **Define inbound ports** in `domain/port/inbound/` — interfaces only
4. **Implement the service** in `application/` — implements inbound port
5. **Implement the adapter** in `infrastructure/` — implements outbound port
6. **Create the controller** in `api/rest/` — depends on inbound port only
7. **Write tests** — unit test for domain, integration test for API
8. **Run Detekt** — fix any violations before committing

---

## Common Mistakes to Avoid

AI agents often generate these patterns that violate project conventions. Watch for and correct them:

### Variable Declaration
- **Generating `var` instead of `val`** — Use immutable variables by default. The `VarCouldBeVal` detekt rule catches this.
- **Using mutable collections** — Prefer `listOf()`, `setOf()`, `mapOf()` over mutable variants.

### Utility Functions
- **Creating utility classes with static methods** — Use extension functions or top-level functions instead.
  ```kotlin
  // Bad
  object StringUtils {
      fun String.isEmptyOrNull() = this.isNullOrBlank()
  }
  // Good
  fun String.isEmptyOrNull() = this.isNullOrBlank()
  ```

### Property Access
- **Using Java-style getters/setters** — Use Kotlin properties directly. For DTOs, use data classes.
  ```kotlin
  // Bad
  person.getName()
  person.setName(name)
  // Good
  person.name
  ```

### Null Safety
- **Ignoring null safety** — Never use `!!` (non-null assertion). Prefer safe calls `?.` and elvis `?:`.
  ```kotlin
  // Bad
  val length = string!!.length
  // Good
  val length = string?.length ?: 0
  ```

### Testing Anti-Patterns
- **Writing tests that only verify mock behavior** — Test real logic, not mock interactions. Use `verify()` sparingly.
- **Skipping ArchUnit tests** — Never disable architecture tests when "just adding a quick feature."

### Dependencies
- **Adding unnecessary dependencies** — Check if Kotlin stdlib already provides it before adding a library.

---

## Testing Patterns

### Test Structure: Given-When-Then

Use the Given-When-Then pattern for clear, readable test bodies:

```kotlin
@Test
fun `should return greeting when valid id provided`() {
    // Given
    val id = "test-id"
    val expected = Greeting(id, "Hello, test-id!")

    // When
    val result = greetingService.getGreeting(id)

    // Then
    assertEquals(expected, result)
}
```

### Test Naming

- Use **backtick style** for test names describing expected behavior
- Format: `should {expected outcome} when {condition}`
- Examples:
  - `should return greeting when valid id provided`
  - `should throw exception when id is empty`
  - `should return empty list when no greetings exist`

### Integration Tests

- Use **Ktor HttpClient** (not MockMvc) — see `BaseIntegrationTest`
- Test full HTTP requests/responses
- Verify status codes, headers, and body
- Test against real endpoints (in-memory database)

### Architecture Tests

- Located in `src/test/kotlin/.../archunit/`
- Enforce hexagonal boundary rules
- Run on every build — never skip
- See `ArchitectureTest` for examples

### Coverage Requirements

- Every public service method must have at least one test
- Domain layer tests must have ZERO Spring dependencies
- Test boundary conditions, not just happy paths

---

## Error Handling

### Simple Cases: Domain Exceptions

For straightforward error scenarios, throw domain-specific exceptions:

```kotlin
fun getGreeting(id: String): Greeting {
    if (id.isBlank()) {
        throw IllegalArgumentException("ID cannot be blank")
    }
    return repository.findById(id)
        ?: throw NoSuchElementException("Greeting not found: $id")
}
```

### Complex Scenarios: Result or Either

For more complex error handling with multiple failure modes:

**Option 1: Kotlin's `Result<T>`**
```kotlin
fun getGreeting(id: String): Result<Greeting> {
    return when {
        id.isBlank() -> Result.failure(IllegalArgumentException("ID cannot be blank"))
        else -> repository.findById(id)?.let { Result.success(it) }
            ?: Result.failure(NoSuchElementException("Greeting not found: $id"))
    }
}
```

**Option 2: Sealed class `Either<L, R>`**
```kotlin
sealed interface ApiResult<out T> {
    data class Success<T>(val value: T) : ApiResult<T>
    data class Error(val message: String, val cause: Throwable? = null) : ApiResult<Nothing>
}

fun getGreeting(id: String): ApiResult<Greeting> {
    return when {
        id.isBlank() -> ApiResult.Error("ID cannot be blank")
        else -> repository.findById(id)?.let { ApiResult.Success(it) }
            ?: ApiResult.Error("Greeting not found: $id")
    }
}
```

### Error Handling Rules

- **Never swallow exceptions silently** — No empty catch blocks
- **Domain exceptions carry meaningful messages** — Explain what went wrong
- **Log at appropriate boundaries** — Use SLF4J, not `println()`
- **Prefer specific exceptions** — Catch `IllegalArgumentException`, not `Exception`

---

## Build Commands

```bash
# Build the project
./gradlew build

# Run all tests (including architecture tests)
./gradlew test

# Run static analysis
./gradlew detekt

# Run everything (build + test + detekt)
./gradlew build detekt

# Run only architecture tests
./gradlew test --tests "com.harness.archunit.*"
```
