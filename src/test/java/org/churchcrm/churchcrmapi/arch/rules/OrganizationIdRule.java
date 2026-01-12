package org.churchcrm.churchcrmapi.arch.rules;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaMethod;
import com.tngtech.archunit.core.domain.JavaParameter;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import org.churchcrm.churchcrmapi.crosscutting.web.OrganizationId;

import java.util.UUID;

/**
 * ArchUnit rule to enforce that all parameters annotated with @OrganizationId
 * must be of type UUID.
 * 
 * <p>This ensures type safety and consistency across the application when
 * extracting organization IDs from JWT tokens.</p>
 * 
 * <p>Example of correct usage:</p>
 * <pre>
 * {@code
 * public ChurchDto getChurch(
 *     @PathVariable UUID id,
 *     @OrganizationId UUID organizationId  // ✓ Correct - UUID type
 * ) { ... }
 * }
 * </pre>
 * 
 * <p>Example of incorrect usage that will fail this rule:</p>
 * <pre>
 * {@code
 * public ChurchDto getChurch(
 *     @PathVariable UUID id,
 *     @OrganizationId String organizationId  // ✗ Wrong - String type
 * ) { ... }
 * }
 * </pre>
 */
public class OrganizationIdRule extends ArchCondition<JavaClass> {

    public OrganizationIdRule() {
        super("have all @OrganizationId parameters with UUID type");
    }

    @Override
    public void check(JavaClass javaClass, ConditionEvents events) {
        // Check all methods in the class
        for (JavaMethod method : javaClass.getMethods()) {
            // Check all parameters in each method
            int paramIndex = 0;
            for (JavaParameter parameter : method.getParameters()) {
                // If parameter is annotated with @OrganizationId
                if (parameter.isAnnotatedWith(OrganizationId.class)) {
                    // Verify the parameter type is UUID
                    if (!parameter.getRawType().isEquivalentTo(UUID.class)) {
                        String message = String.format(
                                "Parameter at index %d in method '%s.%s' is annotated with @OrganizationId " +
                                "but has type '%s' instead of UUID",
                                paramIndex,
                                javaClass.getSimpleName(),
                                method.getName(),
                                parameter.getRawType().getSimpleName()
                        );
                        events.add(SimpleConditionEvent.violated(javaClass, message));
                    }
                }
                paramIndex++;
            }
        }
    }
}
