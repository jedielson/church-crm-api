package org.churchcrm.churchcrmapi.arch.rules;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.churchcrm.churchcrmapi.crosscutting.web.OrganizationId;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests to verify the OrganizationIdRule correctly detects violations.
 */
class OrganizationIdRuleTest {

    /**
     * Test that the rule passes when @OrganizationId is used with UUID.
     */
    @Test
    void shouldPassWhenOrganizationIdUsesUuid() {
        JavaClasses classes = new ClassFileImporter()
                .importClasses(CorrectUsageExample.class);

        ArchRule rule = classes()
                .should(new OrganizationIdRule());

        // Should not throw any exception
        rule.check(classes);
    }

    /**
     * Test that the rule fails when @OrganizationId is used with wrong type.
     */
    @Test
    void shouldFailWhenOrganizationIdUsesNonUuidType() {
        JavaClasses classes = new ClassFileImporter()
                .importClasses(IncorrectUsageExample.class);

        ArchRule rule = classes()
                .should(new OrganizationIdRule());

        // Should throw AssertionError with descriptive message
        AssertionError error = assertThrows(AssertionError.class, () -> rule.check(classes));
        
        // Verify the error message is descriptive
        String errorMessage = error.getMessage();
        assert errorMessage.contains("@OrganizationId");
        assert errorMessage.contains("String");
        assert errorMessage.contains("instead of UUID");
    }

    // ========================================================================
    // Test Helper Classes
    // ========================================================================

    /**
     * Example of CORRECT usage - @OrganizationId with UUID type.
     */
    static class CorrectUsageExample {
        public void methodWithCorrectType(@OrganizationId UUID organizationId) {
            // Correct usage
        }
    }

    /**
     * Example of INCORRECT usage - @OrganizationId with String type.
     * This should be caught by the ArchUnit rule.
     */
    static class IncorrectUsageExample {
        public void methodWithWrongType(@OrganizationId String organizationId) {
            // Wrong usage - should fail the ArchUnit rule
        }
    }
}
