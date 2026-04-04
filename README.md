# Kotlin Spring Harness

[![Build](https://img.shields.io/github/actions/workflow/status/acailic/kotlin-spring-harness/ci.yml?branch=main&style=flat-square&logo=github)](https://github.com/acailic/kotlin-spring-harness/actions)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.1.x-7F52FF?style=flat-square&logo=kotlin)](https://kotlinlang.org)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.4.x-6DB33F?style=flat-square&logo=spring)](https://spring.io/projects/spring-boot)
[![License: MIT](https://img.shields.io/badge/License-MIT-blue?style=flat-square)](LICENSE)

**AI-ready Kotlin + Spring Boot template with architecture governance built in.**

Skip 3-5 days of boilerplate setup. Click "Use this template" and start writing business logic immediately.

---

## What Is This?

A GitHub template repository that provides a production-ready foundation for Kotlin + Spring Boot projects. Unlike empty skeletons, this harness includes:

- **Architecture enforcement** — ArchUnit tests prevent layer violations at build time
- **AI context engineering** — `AGENTS.md` teaches AI assistants your architecture, so generated code follows your conventions
- **Static analysis** — Detekt with educational rules catches code smells before review
- **Integration testing** — Ktor HttpClient tests real HTTP, not mocked Spring internals

## Why This Harness Exists

### The Problem

Developers waste days on setup that adds no business value:

| Task | Time Wasted |
|---|---|
| Configuring Detekt with meaningful rules | 4-6 hours |
| Writing ArchUnit tests for architecture | 3-4 hours |
| Setting up integration test infrastructure | 2-3 hours |
| Configuring CI pipeline | 1-2 hours |
| Writing AGENTS.md for AI assistants | 2-3 hours |
| **Total** | **12-18 hours** |

### The Solution

Everything above is pre-configured and enforced. You write business logic from day one, with the confidence that your architecture won't erode.

## Quick Start

### 1. Create Your Repository

Click **"Use this template"** on GitHub to create your own repository from this template.

### 2. Generate the Gradle Wrapper

```bash
# If you have Gradle installed locally
gradle wrapper

# Or install SDKMAN and use it
sdk install gradle 8.12
gradle wrapper
```

### 3. Install Git Hooks (Optional)

```bash
./.git-hooks/setup.sh   # Install pre-commit hook for detekt + ArchUnit
```

### 4. Build and Test

```bash
./gradlew build        # Compile + test
./gradlew detekt       # Static analysis
./gradlew test         # Run all tests (including architecture tests)
```

### 5. Start Developing

The template includes a working example (Greeting API) that demonstrates all architectural layers. Replace it with your own domain logic following the patterns established in `AGENTS.md`.

## Architecture

This project uses **Hexagonal Architecture** (Ports & Adapters):

```
┌──────────────────────────────────────────────┐
│              API Layer                        │
│   Controllers, DTOs, REST endpoints           │
├──────────────────────────────────────────────┤
│           Application Layer                   │
│   Services, use case implementations          │
├──────────────────────────────────────────────┤
│             Domain Layer                      │
│   Models, value objects, port interfaces      │
│   (pure Kotlin — zero framework dependencies)  │
├──────────────────────────────────────────────┤
│          Infrastructure Layer                 │
│   Repository implementations, adapters         │
└──────────────────────────────────────────────┘
```

**Key rule:** The domain layer has ZERO dependencies on Spring or any framework. ArchUnit tests enforce this at build time.

## What's Included

### Architecture Governance (`ArchUnit`)

Automated tests that fail your build if you violate architectural boundaries:

- Domain layer cannot import Spring Framework
- API layer cannot access infrastructure directly
- Controllers must depend on ports, not concrete services
- No cyclic dependencies between layers
- Naming conventions enforced (Controller, Service, Repository suffixes)

### Static Analysis (`Detekt`)

Strict rules with educational comments explaining **why** each rule exists:

- Complexity limits (method length, nesting depth, cognitive complexity)
- Coroutines best practices (no GlobalScope, inject dispatchers)
- Performance (no forEach on ranges, no unnecessary spread operator)
- Style (no magic numbers, no wildcard imports, line length 120 chars)
- Bug prevention (no duplicate when branches, no floating point equality)

### AI Context (`AGENTS.md`)

A system prompt file that teaches AI assistants (Cursor, Copilot, Claude) your project's conventions:

- Technology stack and versions
- Architecture rules and layer dependencies
- Coding do's and don'ts
- Testing standards and patterns
- File naming conventions

### Integration Testing (`Ktor HttpClient`)

Uses Ktor's lightweight HTTP client instead of Spring's MockMvc:

- Tests real HTTP protocol (headers, status codes, serialization)
- Idiomatic Kotlin with coroutine support
- Framework-agnostic client — tests what the user actually sends

## Customizing for Your Project

### Package Rename

To adapt this template to your organization, rename `com.harness` to your own package:

**Using IntelliJ IDEA:**
1. Right-click the `com.harness` package → Refactor → Rename
2. Enter your new package name
3. Update `src/main/resources/application.properties` if the package reference changes

**Using find-and-replace (quick setup):**
```bash
# Replace across all source files
find . -name "*.kt" -type f -exec sed -i 's/com\.harness/com.yourcompany/g' {} +
```

### Inspiration

This harness is inspired by Martin Fowler's article **"Harness Engineering for Coding Agent Users"** (April 2026). The concept of providing AI assistants with rich architectural context through `AGENTS.md` enables coding agents to generate convention-compliant code from day one.

### Adding a Real Database

The template includes `InMemoryGreetingRepository` for immediate development and testing. To switch to PostgreSQL or another database:

1. Implement the `GreetingRepository` interface in `infrastructure/persistence/`
2. Add Spring Data JPA or Exposed dependencies to `build.gradle.kts`
3. Configure the datasource in `application.properties`

The hexagonal architecture ensures your application layer remains unchanged — only the infrastructure adapter differs.

## Project Structure

```
src/main/kotlin/com/harness/
├── config/                    # Spring @Configuration
├── domain/                    # Pure Kotlin (ArchUnit guarded)
│   ├── model/                 # Entities, value objects, enums
│   └── port/
│       ├── inbound/           # Use case interfaces
│       └── outbound/          # Repository interfaces
├── application/               # Use case implementations
├── infrastructure/            # Adapters (DB, external APIs)
│   └── persistence/           # Repository implementations
└── api/                       # REST layer
    └── rest/                  # Controllers, DTOs

src/test/kotlin/com/harness/
├── archunit/                  # Architecture governance tests
├── integration/               # Ktor HttpClient integration tests
└── unit/                      # Pure unit tests (no Spring)
```

## Build Commands

```bash
./gradlew build                  # Full build with tests
./gradlew test                   # Run tests only
./gradlew detekt                 # Run static analysis
./gradlew test --tests "*.archunit.*"  # Architecture tests only
./gradlew build detekt           # Build + lint (CI equivalent)
```

## Tech Stack

| Technology | Version | Role |
|---|---|---|
| Kotlin | 2.1.x | Language |
| Java | 21 | Runtime |
| Spring Boot | 3.4.x | Framework |
| Gradle | 8.12 (Kotlin DSL) | Build |
| Detekt | 1.23.x | Static analysis |
| ArchUnit | 1.4.x | Architecture tests |
| Ktor | 3.0.x | Test HTTP client |
| JUnit 5 | — | Test runner |

## Who Is This For?

- **Kotlin developers** learning how production projects are structured
- **Startup engineers** who need to ship fast without accumulating tech debt
- **Job candidates** who want an impressive open-source contribution demonstrating systems thinking

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing`)
3. Follow `AGENTS.md` conventions
4. Ensure `./gradlew build detekt` passes
5. Open a Pull Request

## License

[MIT](LICENSE) — Use it however you like.
