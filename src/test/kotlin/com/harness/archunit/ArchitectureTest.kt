package com.harness.archunit

import com.tngtech.archunit.core.domain.JavaClasses
import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes
import com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeAll

/**
 * Architecture governance tests using ArchUnit.
 *
 * These tests ENFORCE the Hexagonal Architecture boundaries.
 * If any test fails, it means a layer violation has been introduced.
 * Fix the violation, do NOT disable the test.
 *
 * Layers:
 *   - domain:      Pure Kotlin, ZERO Spring/framework dependencies
 *   - application: Use case implementations, may use Spring DI
 *   - infrastructure: Outbound adapters (DB, external APIs)
 *   - api:          REST controllers, DTOs
 *   - config:       Spring configuration
 *
 * Dependency rules:
 *   - domain <- application <- api
 *   - domain <- infrastructure (implements ports)
 *   - api must NOT access infrastructure directly
 *   - domain must NOT depend on any outer layer
 */
class ArchitectureTest {

    companion object {
        private lateinit var importedClasses: JavaClasses

        @BeforeAll
        @JvmStatic
        fun importClasses() {
            importedClasses = ClassFileImporter()
                .importPackages("com.harness")
        }
    }

    // ── Rule 1: Domain layer is pure Kotlin ──────────────────────────────

    @Test
    fun `domain layer must not depend on Spring Framework`() {
        noClasses()
            .that().resideInAPackage("..domain..")
            .should().dependOnClassesThat()
            .resideInAPackage("org.springframework..")
            .check(importedClasses)
    }

    @Test
    fun `domain layer must not depend on infrastructure`() {
        noClasses()
            .that().resideInAPackage("..domain..")
            .should().dependOnClassesThat()
            .resideInAPackage("..infrastructure..")
            .check(importedClasses)
    }

    @Test
    fun `domain layer must not depend on API layer`() {
        noClasses()
            .that().resideInAPackage("..domain..")
            .should().dependOnClassesThat()
            .resideInAPackage("..api..")
            .check(importedClasses)
    }

    // ── Rule 2: API layer must not access infrastructure directly ─────────

    @Test
    fun `API layer must not depend on infrastructure`() {
        noClasses()
            .that().resideInAPackage("..api..")
            .should().dependOnClassesThat()
            .resideInAPackage("..infrastructure..")
            .check(importedClasses)
    }

    // ── Rule 3: Controllers depend on ports, not services ────────────────

    @Test
    fun `controllers must depend on inbound ports`() {
        classes()
            .that().resideInAPackage("..api..")
            .should().dependOnClassesThat()
            .resideInAPackage("..domain.port.inbound..")
            .check(importedClasses)
    }

    // ── Rule 4: No cyclic dependencies between layers ────────────────────

    @Test
    fun `layer slices must be free of cycles`() {
        slices()
            .matching("com.harness.(*)..")
            .should().beFreeOfCycles()
            .check(importedClasses)
    }

    // ── Rule 5: Naming conventions ───────────────────────────────────────

    @Test
    fun `controllers must be named with Controller suffix`() {
        classes()
            .that().resideInAPackage("..api.rest..")
            .and().arePublic()
            .should().haveSimpleNameEndingWith("Controller")
            .check(importedClasses)
    }

    @Test
    fun `repositories must be named with Repository suffix`() {
        classes()
            .that().resideInAPackage("..infrastructure.persistence..")
            .and().arePublic()
            .should().haveSimpleNameEndingWith("Repository")
            .check(importedClasses)
    }

    @Test
    fun `services must be named with Service suffix`() {
        classes()
            .that().resideInAPackage("..application..")
            .and().arePublic()
            .should().haveSimpleNameEndingWith("Service")
            .check(importedClasses)
    }
}
