package org.churchcrm.churchcrmapi.arch;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.ConditionEvents;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import org.churchcrm.churchcrmapi.arch.rules.AuditRule;
import org.churchcrm.churchcrmapi.arch.rules.AuditingEntityListenerRule;
import org.churchcrm.churchcrmapi.arch.rules.ControllerResponseRule;
import org.churchcrm.churchcrmapi.arch.rules.InheritanceRule;
import org.churchcrm.churchcrmapi.arch.rules.OrganizationIdRule;
import org.churchcrm.churchcrmapi.crosscutting.auditing.Audit;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.fields;

/**
 * ArchUnit tests to enforce architectural rules across the codebase.
 * These tests act as compile-time validators similar to C# Roslyn analyzers,
 * ensuring consistency and best practices in entity design.
 */
class ArchitectureTest {

    private static JavaClasses importedClasses;

    @BeforeAll
    static void setup() {
        importedClasses = new ClassFileImporter()
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                .importPackages("org.churchcrm.churchcrmapi");
    }

    /**
     * Rule 1: All @Entity classes must have an @Embedded Audit field.
     * Exceptions:
     * - Classes in the crosscutting package (infrastructure/shared concerns)
     * - Spring Modulith's EventPublication entity
     */
    @Test
    void entitiesShouldHaveEmbeddedAuditField() {
        ArchRule rule = classes()
                .that().areAnnotatedWith(Entity.class)
                .and().resideOutsideOfPackage("..crosscutting..")
                .and().haveNameNotMatching(".*EventPublication.*")
                .should(new AuditRule());

        rule.check(importedClasses);
    }

    /**
     * Rule 2: All @Entity classes must use UUID for their @Id field.
     * This ensures consistency across all domain entities and prepares
     * the system for distributed architectures.
     */
    @Test
    void entitiesShouldUseUuidForId() {
        ArchRule rule = fields()
                .that().areAnnotatedWith(Id.class)
                .and().areDeclaredInClassesThat().areAnnotatedWith(Entity.class)
                .and().areDeclaredInClassesThat().resideOutsideOfPackage("..crosscutting..")
                .should().haveRawType(UUID.class);

        rule.check(importedClasses);
    }

    /**
     * Rule 3: The Audit class must be @Embeddable.
     * Validates that the audit infrastructure is properly configured
     * as an embeddable component.
     */
    @Test
    void auditClassShouldBeEmbeddable() {

        ArchRule rule = classes()
                .that().haveSimpleName("Audit")
                .and().resideInAPackage("..auditing..")
                .should().beAnnotatedWith(Embeddable.class);

        rule.check(importedClasses);
    }

    /**
     * Rule 4: No entity should use inheritance (abstract base classes).
     * This enforces composition over inheritance for cleaner OOP design.
     * All entities should directly extend Object only.
     * Exceptions:
     * - Classes in crosscutting package (may have legitimate infrastructure inheritance)
     */
    @Test
    void entitiesShouldNotUseInheritance() {
        ArchRule rule = classes()
                .that().areAnnotatedWith(Entity.class)
                .and().resideOutsideOfPackage("..crosscutting..")
                .should(new InheritanceRule());

        rule.check(importedClasses);
    }

    /**
     * Rule 5: Audit field naming convention.
     * All @Embedded Audit fields should be named "audit" for consistency.
     */
    @Test
    void auditFieldShouldBeNamedConsistently() {
        ArchRule rule = fields()
                .that().areAnnotatedWith(Embedded.class)
                .and().haveRawType(Audit.class)
                .should().haveName("audit");

        rule.check(importedClasses);
    }

    /**
     * Rule 6: All @Entity classes must have @EntityListeners(AuditingEntityListener.class).
     * This ensures JPA auditing is properly enabled for all entities.
     * Without this annotation, audit fields (created_at, updated_at, etc.) won't be populated.
     * Exceptions:
     * - Classes in the crosscutting package (infrastructure/shared concerns)
     * - Spring Modulith's EventPublication entity
     */
    @Test
    void entitiesShouldHaveEntityListenersAnnotation() {
        ArchRule rule = classes()
                .that().areAnnotatedWith(Entity.class)
                .and().resideOutsideOfPackage("..crosscutting..")
                .and().haveNameNotMatching(".*EventPublication.*")
                .should(new AuditingEntityListenerRule());

        rule.check(importedClasses);
    }

    /**
     * Rule 7: All parameters annotated with @OrganizationId must be of type UUID.
     * This ensures type safety when extracting organization IDs from JWT tokens.
     * The OrganizationIdArgumentResolver expects UUID type for proper conversion.
     */
    @Test
    void organizationIdParametersShouldBeUuid() {
        ArchRule rule = classes()
                .that().resideInAPackage("org.churchcrm.churchcrmapi..")
                .should(new OrganizationIdRule());

        rule.check(importedClasses);
    }

    /**
     * Rule 8: All controller methods must return ResponseEntity.
     * This ensures consistent response handling with explicit HTTP status codes,
     * custom headers when needed, and better control over the response structure.
     * 
     * Benefits:
     * - Explicit status codes (ResponseEntity.ok(), ResponseEntity.created(), etc.)
     * - Ability to add custom headers
     * - Better testability
     * - Consistent API response patterns
     */
    @Test
    void controllerMethodsShouldReturnResponseEntity() {
        ArchRule rule = classes()
                .that().resideInAPackage("org.churchcrm.churchcrmapi..")
                .should(new ControllerResponseRule());

        rule.check(importedClasses);
    }
}
